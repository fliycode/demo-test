# 权限管理系统

## 项目简介

这是一个基于Spring Boot + MyBatis Plus + Redis的完整权限管理系统，实现了RBAC (Role-Based Access Control) 权限模型。系统提供了用户管理、角色管理、权限管理的完整功能，支持基于注解的权限控制。

## 技术栈

- **后端框架**: Spring Boot 3.5.3
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis Plus 3.5.12
- **缓存**: Redis
- **认证方式**: Token + Redis
- **权限控制**: AOP + 自定义注解
- **密码加密**: BCrypt
- **构建工具**: Maven

## 系统特性

✅ **完整的RBAC权限模型**
- 用户(User) - 角色(Role) - 权限(Permission)
- 支持用户多角色，角色多权限

✅ **安全的认证机制**
- JWT Token认证
- Redis存储Token，支持过期和续期
- BCrypt密码加密

✅ **灵活的权限控制**
- 基于注解的权限校验 `@RequirePermission`
- AOP切面自动拦截权限检查
- 权限缓存机制，提高性能

✅ **完善的CRUD操作**
- 用户管理（增删改查、角色分配）
- 角色管理（增删改查、权限分配）
- 权限管理（增删改查、树形结构）

✅ **统一的响应格式**
- 统一的Result返回格式
- 全局异常处理
- 详细的错误信息

## 项目结构

```
src/main/java/com/xm/demotest/
├── common/                 # 公共组件
│   ├── Result.java        # 统一返回结果
│   ├── annotation/        # 自定义注解
│   └── exception/         # 异常处理
├── config/                # 配置类
│   ├── aspect/           # AOP切面
│   ├── filter/           # 拦截器
│   ├── MybatisConfig.java # MyBatis配置
│   └── WebConfig.java    # Web配置
├── controller/           # 控制器
│   ├── auth/            # 认证相关
│   └── user/            # 用户、角色、权限
├── mapper/              # 数据访问层
├── pojo/               # 实体类
├── service/            # 业务逻辑层
│   ├── auth/          # 认证服务
│   ├── user/          # 用户相关服务
│   └── Impl/          # 服务实现
└── utils/             # 工具类
```

## 数据库设计

### 核心表结构

1. **user** - 用户表
   - id, username, password, phone, name, status

2. **role** - 角色表
   - id, role_name, role_key

3. **permission** - 权限表
   - id, parent_id, permission_name, permission_str

4. **user_role** - 用户角色关联表
   - user_id, role_id

5. **role_permission** - 角色权限关联表
   - role_id, permission_id

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 数据库初始化

1. 创建数据库 `demo`
2. 执行提供的数据库脚本创建表结构
3. 运行 `init_permissions.sql` 初始化权限数据

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

### 4. 启动应用

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动

### 5. 测试接口

#### 登录获取Token
```bash
curl -X POST http://localhost:8080/user/login \
  -d "username=admin&password=123456"
```

#### 访问受保护接口
```bash
curl -X GET http://localhost:8080/user/list \
  -H "Authorization: your-token-here"
```

## 权限使用说明

### 1. 权限注解使用

在需要权限控制的方法上添加 `@RequirePermission` 注解：

```java
@GetMapping("/list")
@RequirePermission("user:list")
public Result<List<User>> getAllUsers() {
    return Result.success(userService.getAllUsers());
}
```

### 2. 权限字符串规范

权限字符串采用 `资源:操作` 的格式：

- `user:view` - 查看用户
- `user:add` - 添加用户
- `user:update` - 更新用户
- `user:delete` - 删除用户
- `role:assign:permission` - 为角色分配权限

### 3. 权限缓存机制

- 用户权限会缓存到Redis中，有效期1小时
- 角色权限变更时会自动清除相关用户的权限缓存
- 支持手动刷新权限缓存

## 系统优势

1. **高性能**: Redis缓存权限信息，减少数据库查询
2. **高安全**: BCrypt密码加密，Token认证机制
3. **易扩展**: 清晰的分层架构，便于功能扩展
4. **易维护**: 统一的异常处理和返回格式
5. **易使用**: 注解式权限控制，简单易用

## API文档

详细的API文档请参考 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 初始账户

- **管理员**: admin / 123456 (拥有所有权限)
- **普通用户**: 可通过注册接口创建

## 开发说明

### 添加新权限

1. 在数据库 `permission` 表中添加权限记录
2. 在需要权限控制的方法上添加 `@RequirePermission("权限字符串")` 注解
3. 为相应角色分配该权限

### 自定义权限校验

系统使用AOP切面进行权限校验，相关代码在 `PermissionAspect` 类中。如需自定义权限校验逻辑，可以修改该类。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 许可证

MIT License