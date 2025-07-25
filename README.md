# 权限管理系统文档

## 一、数据库结构

- user：用户表
- role：角色表
- permission：权限表
- user_role：用户-角色关联表
- role_permission：角色-权限关联表

## 二、接口说明

### 用户相关
- POST   /user/add         新增用户（需要 user:add 权限）
- DELETE /user/delete      删除用户（需要 user:delete 权限）
- PUT    /user/update      更新用户（需要 user:update 权限）
- GET    /user/info        查询用户信息（需要 user:info 权限）

### 角色相关
- POST   /role/add         新增角色（需要 role:add 权限）
- DELETE /role/delete      删除角色（需要 role:delete 权限）
- PUT    /role/update      更新角色（需要 role:update 权限）
- GET    /role/info        查询角色信息（需要 role:info 权限）

### 权限相关
- POST   /permission/add         新增权限（需要 user:per:add 权限）
- DELETE /permission/delete      删除权限（需要 user:per:delete 权限）
- PUT    /permission/update      更新权限（需要 user:per:update 权限）
- GET    /permission/info        查询权限信息（需要 user:per:info 权限）

### 用户-角色分配
- POST   /user-role/assign       给用户分配角色
- POST   /user-role/remove       移除用户角色
- GET    /user-role/list         查询用户所有角色ID

### 角色-权限分配
- POST   /role-permission/assign 给角色分配权限
- POST   /role-permission/remove 移除角色权限
- GET    /role-permission/list   查询角色所有权限ID

## 三、权限模型
- 用户通过 user_role 拥有多个角色
- 角色通过 role_permission 拥有多个权限
- 权限字符串（如 user:add、role:delete）用于接口访问控制

## 四、权限认证机制
- 登录后获取token，token存于redis，拦截器校验token有效性
- 拦截器自动校验用户是否拥有访问接口的权限
- 支持@PermissionRequired注解，方法级权限控制
- 权限字符串可自定义，建议与接口功能一一对应

## 五、如何扩展权限
- 在permission表新增权限记录
- 在controller方法上添加@PermissionRequired("xxx:xxx")注解
- 通过角色-权限分配接口分配权限

## 六、如何使用
1. 启动项目，初始化数据库
2. 通过注册/登录接口获取token
3. 调用接口时在header中带上Authorization: token
4. 按需分配角色和权限

## 七、常见问题
- 权限不足时接口返回403
- 未登录时接口返回401
- 可通过数据库直接维护权限、角色、用户关系

---
如需进一步扩展，请参考各Service和Controller实现。