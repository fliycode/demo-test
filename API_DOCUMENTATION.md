# 权限管理系统 API 文档

## 概述

这是一个基于Spring Boot + MyBatis Plus + Redis的权限管理系统，实现了用户、角色、权限的完整RBAC模型。

## 系统架构

- **认证方式**: Token + Redis
- **权限控制**: 基于注解的AOP权限校验
- **数据库**: MySQL
- **缓存**: Redis
- **ORM**: MyBatis Plus

## 数据库设计

### 核心表结构

1. **user** - 用户表
2. **role** - 角色表  
3. **permission** - 权限表
4. **user_role** - 用户角色关联表
5. **role_permission** - 角色权限关联表

## API 接口

### 1. 认证相关接口

#### 1.1 用户登录
- **URL**: `POST /user/login`
- **描述**: 用户登录获取访问令牌
- **参数**:
  - `username` (string, required): 用户名
  - `password` (string, required): 密码
- **响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "uuid-token-string"
  }
}
```

#### 1.2 用户注册
- **URL**: `POST /user/register`
- **描述**: 用户注册
- **参数**:
  - `username` (string, required): 用户名
  - `password` (string, required): 密码（至少6位）
  - `phone` (string, optional): 手机号
  - `name` (string, optional): 昵称
- **响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "message": "注册成功"
  }
}
```

### 2. 用户管理接口

#### 2.1 添加用户
- **URL**: `POST /user/add`
- **权限**: `user:add`
- **参数**:
  - `username` (string, required): 用户名
  - `password` (string, required): 密码
  - `phone` (string, optional): 手机号
  - `name` (string, optional): 昵称
  - `status` (string, required): 状态 (0-禁用, 1-启用)

#### 2.2 删除用户
- **URL**: `DELETE /user/delete/{userId}`
- **权限**: `user:delete`
- **参数**:
  - `userId` (path): 用户ID

#### 2.3 更新用户
- **URL**: `PUT /user/update`
- **权限**: `user:update`
- **参数**:
  - `username` (string, required): 用户名
  - `password` (string, optional): 新密码
  - `phone` (string, optional): 手机号
  - `name` (string, optional): 昵称
  - `status` (string, optional): 状态

#### 2.4 获取用户信息
- **URL**: `GET /user/info/{userId}`
- **权限**: `user:view`
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "phone": "13111111111",
    "name": "管理员",
    "status": "1"
  }
}
```

#### 2.5 获取用户列表
- **URL**: `GET /user/list`
- **权限**: `user:list`

#### 2.6 分页查询用户
- **URL**: `GET /user/page`
- **权限**: `user:list`
- **参数**:
  - `pageNum` (int, optional, default=1): 页码
  - `pageSize` (int, optional, default=10): 每页大小

#### 2.7 为用户分配角色
- **URL**: `POST /user/{userId}/roles`
- **权限**: `user:assign:role`
- **请求体**:
```json
[1, 2, 3]
```

#### 2.8 获取用户角色
- **URL**: `GET /user/{userId}/roles`
- **权限**: `user:view`

### 3. 角色管理接口

#### 3.1 添加角色
- **URL**: `POST /role/add`
- **权限**: `role:add`
- **参数**:
  - `roleName` (string, required): 角色名称
  - `roleKey` (string, required): 角色标识

#### 3.2 删除角色
- **URL**: `DELETE /role/delete/{roleId}`
- **权限**: `role:delete`

#### 3.3 更新角色
- **URL**: `PUT /role/update/{roleId}`
- **权限**: `role:update`
- **参数**:
  - `roleName` (string, required): 角色名称
  - `roleKey` (string, required): 角色标识

#### 3.4 获取角色信息
- **URL**: `GET /role/{roleId}`
- **权限**: `role:view`

#### 3.5 获取角色列表
- **URL**: `GET /role/list`
- **权限**: `role:list`

#### 3.6 分页查询角色
- **URL**: `GET /role/page`
- **权限**: `role:list`

#### 3.7 为角色分配权限
- **URL**: `POST /role/{roleId}/permissions`
- **权限**: `role:assign:permission`
- **请求体**:
```json
[101, 102, 103]
```

#### 3.8 获取角色权限
- **URL**: `GET /role/{roleId}/permissions`
- **权限**: `role:view`

### 4. 权限管理接口

#### 4.1 添加权限
- **URL**: `POST /permission/add`
- **权限**: `permission:add`
- **参数**:
  - `parentId` (int, optional): 父权限ID
  - `permissionName` (string, required): 权限名称
  - `permissionStr` (string, required): 权限字符串

#### 4.2 删除权限
- **URL**: `DELETE /permission/delete/{permissionId}`
- **权限**: `permission:delete`

#### 4.3 更新权限
- **URL**: `PUT /permission/update/{permissionId}`
- **权限**: `permission:update`

#### 4.4 获取权限信息
- **URL**: `GET /permission/{permissionId}`
- **权限**: `permission:view`

#### 4.5 获取权限列表
- **URL**: `GET /permission/list`
- **权限**: `permission:list`

#### 4.6 获取权限树
- **URL**: `GET /permission/tree`
- **权限**: `permission:list`

### 5. 认证授权接口

#### 5.1 获取当前用户权限
- **URL**: `GET /auth/permissions`
- **描述**: 获取当前登录用户的所有权限

#### 5.2 检查权限
- **URL**: `GET /auth/hasPermission`
- **参数**:
  - `permission` (string, required): 权限字符串

#### 5.3 刷新权限缓存
- **URL**: `POST /auth/refresh`
- **描述**: 刷新当前用户的权限缓存

## 权限字符串说明

### 用户管理权限
- `user:view` - 查看用户信息
- `user:list` - 获取用户列表
- `user:add` - 添加用户
- `user:update` - 更新用户
- `user:delete` - 删除用户
- `user:assign:role` - 为用户分配角色

### 角色管理权限
- `role:view` - 查看角色信息
- `role:list` - 获取角色列表
- `role:add` - 添加角色
- `role:update` - 更新角色
- `role:delete` - 删除角色
- `role:assign:permission` - 为角色分配权限

### 权限管理权限
- `permission:view` - 查看权限信息
- `permission:list` - 获取权限列表
- `permission:add` - 添加权限
- `permission:update` - 更新权限
- `permission:delete` - 删除权限

## 错误码说明

- `200` - 操作成功
- `401` - 未授权（未登录或token无效）
- `403` - 权限不足
- `500` - 服务器内部错误

## 使用示例

### 1. 登录获取token
```bash
curl -X POST http://localhost:8080/user/login \
  -d "username=admin&password=123456"
```

### 2. 使用token访问受保护接口
```bash
curl -X GET http://localhost:8080/user/list \
  -H "Authorization: your-token-here"
```

### 3. 为用户分配角色
```bash
curl -X POST http://localhost:8080/user/4/roles \
  -H "Authorization: your-token-here" \
  -H "Content-Type: application/json" \
  -d "[1, 2]"
```

## 初始化数据

系统提供了初始化数据：
- 管理员用户: `admin/123456` (拥有所有权限)
- 管理员角色: `管理员` (拥有所有权限)
- 普通用户角色: `普通用户` (拥有基础查看权限)

请运行 `init_permissions.sql` 脚本来初始化权限数据。

## 注意事项

1. 所有接口（除登录注册外）都需要在请求头中携带 `Authorization` token
2. Token有效期为1小时，访问接口时会自动续期
3. 权限采用字符串匹配，需要精确匹配权限字符串
4. 删除角色或权限时会自动清理相关的关联关系
5. 用户状态变更或角色权限变更时会自动清除相关缓存