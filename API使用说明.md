# Redis+权限认证系统 API使用说明

## 系统概述

本系统基于Spring Boot + MyBatis-Plus + Redis实现了完整的用户认证和权限管理功能，包括：

- Token-based认证
- Redis缓存权限信息
- 基于注解的权限控制
- 拦截器实现权限验证

## 数据库表结构

系统包含以下核心表：
- `user`: 用户表
- `role`: 角色表  
- `permission`: 权限表
- `user_role`: 用户角色关系表
- `role_permission`: 角色权限关系表

## API接口说明

### 1. 用户认证相关

#### 登录
```
POST /user/login
Content-Type: application/x-www-form-urlencoded

username=admin&password=123456
```

响应：
```json
{
    "token": "生成的token",
    "user_id": "用户ID"
}
```

#### 注册（如果已实现）
```
POST /user/register
```

### 2. 用户信息相关

#### 获取当前用户信息
```
GET /user/info
Authorization: your_token_here
```

#### 获取当前用户权限
```
GET /user/permissions
Authorization: your_token_here
```

#### 获取所有用户列表（需要user:list权限）
```
GET /user/list
Authorization: your_token_here
```

#### 获取用户详情（需要user:per权限）
```
GET /user/detail/{id}
Authorization: your_token_here
```

### 3. 权限管理相关

#### 获取所有权限列表（需要admin:permission:list权限）
```
GET /admin/permission/list
Authorization: your_token_here
```

#### 获取所有角色列表（需要admin:role:list权限）
```
GET /admin/permission/roles
Authorization: your_token_here
```

#### 获取指定用户的权限详情（需要admin:user:permission权限）
```
GET /admin/permission/user/{userId}
Authorization: your_token_here
```

#### 刷新用户权限缓存（需要admin:permission:refresh权限）
```
POST /admin/permission/refresh/{userId}
Authorization: your_token_here
```

#### 清除用户权限缓存（需要admin:permission:clear权限）
```
DELETE /admin/permission/cache/{userId}
Authorization: your_token_here
```

### 4. 认证检查相关

#### 获取用户权限码
```
POST /auth/permissions
Content-Type: application/x-www-form-urlencoded
Authorization: your_token_here

id=用户ID
```

#### 检查用户是否有特定权限
```
GET /auth/hasPermission?id=用户ID&permission=权限码
Authorization: your_token_here
```

## 权限注解使用

在Controller方法上使用`@RequirePermission`注解来控制访问权限：

```java
@GetMapping("/list")
@RequirePermission(value = "user:list", description = "获取所有用户信息")
public Map<String, Object> getAllUsers() {
    // 方法实现
}
```

## 系统架构

### 拦截器链
1. **AuthInterceptor**: 验证Token有效性，提取用户ID
2. **PermissionInterceptor**: 检查用户是否有访问特定接口的权限

### 权限缓存机制
- 用户登录时，系统会将用户权限缓存到Redis
- Redis Key格式: `user:{userId}:permissions`
- 缓存过期时间: 24小时
- 支持手动刷新和清除缓存

### 权限检查流程
1. 请求到达时，AuthInterceptor验证Token
2. PermissionInterceptor检查方法是否有权限注解
3. 如果有权限注解，从Redis获取用户权限
4. 检查用户是否拥有所需权限
5. 权限验证通过则继续执行，否则返回403错误

### 5. 测试接口相关

#### 无需权限的测试接口
```
GET /test/hello
```

#### 查看用户权限信息
```
GET /test/user/permissions/{userId}
```

#### 检查用户是否有特定权限
```
GET /test/user/{userId}/check/{permission}
```

#### 刷新用户权限缓存
```
POST /test/user/{userId}/refresh
```

#### 清除用户权限缓存
```
DELETE /test/user/{userId}/cache
```

#### 查看Redis中的所有数据
```
GET /test/redis/keys
```

#### 获取当前登录用户信息（需要token）
```
GET /test/current/user
Authorization: your_token_here
```

## 测试数据

系统预置了以下测试账号：

| 用户名 | 密码 | 角色 | 权限 |
|-------|------|------|------|
| admin | 123456 | 管理员 | 所有权限（user:list, user:per, user:create, user:update, user:delete, system:permission, system:role等） |
| user  | 123456 | 普通用户 | 基础权限（user:list, user:per） |

## 快速测试步骤

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 初始化数据库
执行 `src/main/resources/init.sql` 脚本来创建数据库表和测试数据。

### 3. 测试基础功能
```bash
# 测试无需权限的接口
curl -X GET http://localhost:8080/test/hello

# 查看管理员用户权限（无需token）
curl -X GET http://localhost:8080/test/user/permissions/1

# 查看普通用户权限（无需token）
curl -X GET http://localhost:8080/test/user/permissions/2
```

### 4. 测试管理员登录
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=123456"
```

### 5. 使用管理员token测试权限接口
```bash
# 替换your_admin_token_here为实际的token
ADMIN_TOKEN="your_admin_token_here"

# 获取用户列表（需要user:list权限）
curl -X GET http://localhost:8080/user/list \
  -H "Authorization: $ADMIN_TOKEN"

# 获取用户详情（需要user:per权限）
curl -X GET http://localhost:8080/user/detail/1 \
  -H "Authorization: $ADMIN_TOKEN"

# 获取当前用户信息
curl -X GET http://localhost:8080/test/current/user \
  -H "Authorization: $ADMIN_TOKEN"
```

### 6. 测试普通用户登录
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user&password=123456"
```

### 7. 使用普通用户token测试权限限制
```bash
# 替换your_user_token_here为实际的token
USER_TOKEN="your_user_token_here"

# 普通用户可以访问的接口
curl -X GET http://localhost:8080/user/list \
  -H "Authorization: $USER_TOKEN"

# 普通用户无法访问的接口（会返回403）
curl -X GET http://localhost:8080/admin/permission/list \
  -H "Authorization: $USER_TOKEN"
```

### 8. 测试权限管理功能
```bash
# 检查用户是否有特定权限
curl -X GET http://localhost:8080/test/user/1/check/user:list
curl -X GET http://localhost:8080/test/user/2/check/system:permission

# 刷新用户权限缓存
curl -X POST http://localhost:8080/test/user/1/refresh

# 清除用户权限缓存
curl -X DELETE http://localhost:8080/test/user/1/cache

# 查看Redis中的数据（会在控制台输出）
curl -X GET http://localhost:8080/test/redis/keys
```

## 错误码说明

- `200`: 成功
- `401`: 未授权（Token无效或未提供）
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 注意事项

1. 所有需要认证的接口都必须在请求头中携带`Authorization`字段
2. Token有效期为1小时，过期后需要重新登录
3. 权限缓存有效期为24小时，可以通过管理接口手动刷新
4. 系统支持层级权限设计，可以根据需要扩展权限粒度