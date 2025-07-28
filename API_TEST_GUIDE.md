# API 测试指南

## 权限系统测试说明

现在系统已经完整实现了JWT认证 + Redis缓存 + RBAC权限控制。以下是测试步骤：

### 1. 准备工作

确保已启动：
- MySQL数据库（执行了init.sql）
- Redis服务器
- Spring Boot应用

### 2. 测试流程

#### 第一步：测试公开接口（无需认证）

```bash
# 健康检查
curl -X GET http://localhost:8080/test/health

# 公开接口
curl -X GET http://localhost:8080/test/public
```

#### 第二步：用户注册（可选）

```bash
curl -X POST http://localhost:8080/user/register \
  -d "username=testuser&password=123456&phone=13888888889&name=测试用户"
```

#### 第三步：用户登录获取Token

```bash
# 使用管理员账号登录
curl -X POST http://localhost:8080/user/login \
  -d "username=admin&password=admin123"
```

预期响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "name": "管理员",
    "permissions": ["user:view", "user:list", "user:add", ...]
  }
}
```

**重要：保存返回的token，后续请求需要使用**

#### 第四步：测试需要权限的接口

使用token访问受保护的接口：

```bash
# 测试需要user:view权限的接口
curl -X GET http://localhost:8080/test/protected \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 测试需要user:delete权限的接口  
curl -X GET http://localhost:8080/test/admin \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 获取用户列表（需要user:list权限）
curl -X GET http://localhost:8080/user/list \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 添加用户（需要user:add权限）
curl -X POST http://localhost:8080/user/add \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d "username=newuser&password=123456&phone=13999999999&name=新用户"
```

#### 第五步：测试权限验证

使用普通用户账号测试：

```bash
# 先注册并登录普通用户
curl -X POST http://localhost:8080/user/register \
  -d "username=normaluser&password=123456&phone=13777777777&name=普通用户"

curl -X POST http://localhost:8080/user/login \
  -d "username=normaluser&password=123456"

# 使用普通用户token访问管理员接口（应该返回403）
curl -X GET http://localhost:8080/test/admin \
  -H "Authorization: Bearer NORMAL_USER_TOKEN"

curl -X POST http://localhost:8080/user/add \
  -H "Authorization: Bearer NORMAL_USER_TOKEN" \
  -d "username=test&password=123456&phone=13666666666&name=测试"
```

### 3. 权限分配说明

系统预置了两个角色：
- **管理员角色（ID: 1）**: 拥有所有权限
- **普通用户角色（ID: 2）**: 只有基础查看权限

权限列表：
```
用户管理权限：
- user:view (查看用户)
- user:list (用户列表) 
- user:add (添加用户)
- user:update (更新用户)
- user:delete (删除用户)
- user:assign:role (分配角色)

角色管理权限：
- role:view, role:list, role:add, role:update, role:delete, role:assign:permission

权限管理权限：
- permission:view, permission:list, permission:add, permission:update, permission:delete
```

### 4. 错误响应说明

#### 401 未认证
```json
{
  "code": 401,
  "message": "Token invalid or expired",
  "data": null
}
```

#### 403 权限不足
```json
{
  "code": 403,
  "message": "权限不足，需要权限: user:add",
  "data": null
}
```

### 5. 其他功能测试

#### 权限管理接口
```bash
# 获取当前用户权限
curl -X GET http://localhost:8080/auth/permissions \
  -H "Authorization: Bearer YOUR_TOKEN"

# 检查特定权限
curl -X GET "http://localhost:8080/auth/hasPermission?permission=user:add" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 刷新权限缓存
curl -X POST http://localhost:8080/auth/refresh \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 用户管理接口
```bash
# 分页查询用户
curl -X GET "http://localhost:8080/user/page?current=1&size=10&keyword=admin" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 更新用户状态
curl -X PUT http://localhost:8080/user/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "userId=2&status=0"

# 分配角色
curl -X POST http://localhost:8080/user/assignRoles \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d "userId=2&roleIds=1"
```

#### 退出登录
```bash
curl -X POST http://localhost:8080/user/logout \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 6. 常见问题

**Q: 为什么返回403权限不足？**
A: 检查当前用户是否有对应的权限，可以通过`/auth/permissions`接口查看当前用户拥有的所有权限。

**Q: Token过期怎么办？**
A: 重新登录获取新token，或者使用`/user/refreshToken`接口刷新token。

**Q: 如何给用户分配更多权限？**
A: 使用管理员账号调用`/user/assignRoles`接口为用户分配管理员角色。

### 7. 调试技巧

1. 查看控制台日志，JWT解析和权限验证过程会有详细日志
2. 检查Redis中的缓存数据：
   - Token: `token:YOUR_TOKEN`
   - 用户权限: `user:permissions:USER_ID`
3. 数据库查询用户角色权限关系
4. 使用Postman等工具进行接口测试，更方便管理Headers

这样你就可以完整测试整个权限系统的功能了！