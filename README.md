# Spring Boot JWT + Redis + RBAC 权限管理系统

这是一个基于Spring Boot 3.x的权限管理系统，集成了JWT认证、Redis缓存和RBAC权限控制模型。

## 功能特性

- **JWT认证**: 使用JSON Web Token进行用户身份认证
- **Redis缓存**: 缓存用户权限信息，提高系统性能
- **RBAC权限模型**: 基于角色的访问控制，支持用户-角色-权限的多级管理
- **MyBatis-Plus**: 简化数据库操作，支持分页查询
- **RESTful API**: 标准的REST风格接口设计
- **BCrypt密码加密**: 安全的密码存储

## 技术栈

- Spring Boot 3.5.3
- Spring Security 6.x
- JWT (jsonwebtoken 0.11.5)
- Redis
- MyBatis-Plus 3.5.12
- MySQL 8.0
- Lombok
- Jackson

## 项目结构

```
src/main/java/com/xm/demotest/
├── common/                 # 通用响应类
├── config/                 # 配置类
│   ├── fliter/            # 过滤器
│   ├── MybatisConfig.java
│   ├── RedisConfig.java
│   └── SecurityConfig.java
├── controller/            # 控制器层
│   ├── auth/              # 认证相关
│   └── user/              # 用户管理
├── mapper/                # 数据访问层
├── pojo/                  # 实体类
├── service/               # 业务逻辑层
│   ├── auth/              # 认证服务
│   ├── user/              # 用户服务
│   └── Impl/              # 服务实现
└── utils/                 # 工具类
```

## 数据库设计

### 核心表结构

- **user**: 用户表
- **role**: 角色表
- **permission**: 权限表
- **user_role**: 用户角色关联表
- **role_permission**: 角色权限关联表

### 权限层次结构

```
用户管理权限 (user:per)
├── 查看用户 (user:view)
├── 用户列表 (user:list)
├── 添加用户 (user:add)
├── 更新用户 (user:update)
├── 删除用户 (user:delete)
└── 分配角色 (user:assign:role)

角色管理权限 (role:per)
├── 查看角色 (role:view)
├── 角色列表 (role:list)
├── 添加角色 (role:add)
├── 更新角色 (role:update)
├── 删除角色 (role:delete)
└── 分配权限 (role:assign:permission)

权限管理权限 (permission:per)
├── 查看权限 (permission:view)
├── 权限列表 (permission:list)
├── 添加权限 (permission:add)
├── 更新权限 (permission:update)
└── 删除权限 (permission:delete)
```

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 数据库初始化

执行 `src/main/resources/sql/init.sql` 文件创建数据库表和初始数据。

默认管理员账号：
- 用户名: admin
- 密码: admin123

### 3. 配置文件

修改 `application.properties` 中的数据库和Redis连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456

# Redis配置
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

## API 接口

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /user/login | 用户登录 |
| POST | /user/logout | 用户退出 |
| POST | /user/register | 用户注册 |
| POST | /user/refreshToken | 刷新Token |

### 用户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /user/add | 添加用户 |
| DELETE | /user/delete/{username} | 删除用户 |
| PUT | /user/update | 更新用户 |
| GET | /user/info/{username} | 获取用户信息 |
| GET | /user/list | 获取用户列表 |
| GET | /user/page | 分页查询用户 |
| POST | /user/assignRoles | 分配角色 |

### 权限接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /auth/permissions | 获取当前用户权限 |
| GET | /auth/hasPermission | 检查权限 |
| POST | /auth/refresh | 刷新权限缓存 |

## 使用示例

### 1. 用户登录

```bash
curl -X POST http://localhost:8080/user/login \
  -d "username=admin&password=admin123"
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "name": "管理员",
    "permissions": ["user:view", "user:list", ...]
  }
}
```

### 2. 访问受保护接口

在请求头中添加Authorization：

```bash
curl -X GET http://localhost:8080/user/list \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. 检查权限

```bash
curl -X GET "http://localhost:8080/auth/hasPermission?permission=user:add" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 配置说明

### JWT配置

```properties
# JWT密钥（生产环境请使用更复杂的密钥）
jwt.secret=YourSecretKeyMustBeAtLeast256BitsLongForHMACAlgorithmsLikeHS256
# Token过期时间（毫秒，默认24小时）
jwt.expiration=86400000
# 请求头名称
jwt.header=Authorization
# Token前缀
jwt.prefix=Bearer 
```

### Redis配置

```properties
spring.data.redis.database=0
spring.data.redis.password=
spring.data.redis.jedis.pool.max-active=8
spring.data.redis.jedis.pool.max-wait=-1
spring.data.redis.jedis.pool.max-idle=8
spring.data.redis.jedis.pool.min-idle=0
```

## 安全机制

1. **密码加密**: 使用BCrypt算法加密存储密码
2. **Token验证**: JWT Token存储在Redis中，支持主动失效
3. **权限缓存**: 用户权限缓存在Redis中，减少数据库查询
4. **CORS支持**: 支持跨域请求配置
5. **SQL注入防护**: 使用MyBatis-Plus参数化查询

## 扩展功能

### 1. 添加新权限

在数据库中插入新的权限记录：

```sql
INSERT INTO permission (parent_id, permission_name, permission_str) 
VALUES (100, '导出用户', 'user:export');
```

### 2. 自定义权限注解

可以创建方法级别的权限注解：

```java
@PreAuthorize("hasPermission('user:add')")
@PostMapping("/add")
public Result<String> addUser(...) {
    // 方法实现
}
```

### 3. 权限缓存策略

权限信息默认缓存24小时，可以通过以下方式刷新：

- 用户重新登录
- 调用权限刷新接口
- 修改用户角色后自动清除缓存

## 注意事项

1. **生产环境部署**:
   - 修改JWT密钥为更安全的值
   - 配置HTTPS
   - 设置合适的CORS策略

2. **性能优化**:
   - 合理设置Redis缓存过期时间
   - 使用数据库连接池
   - 考虑权限数据的分级缓存

3. **监控建议**:
   - 监控Redis连接状态
   - 记录认证失败日志
   - 监控API调用频率

## 许可证

MIT License