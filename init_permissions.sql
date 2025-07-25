-- 清空现有数据（可选）
DELETE FROM role_permission;
DELETE FROM user_role;
DELETE FROM permission WHERE id > 100;
DELETE FROM role WHERE id > 2;

-- 插入权限数据
INSERT INTO permission (id, parent_id, permission_name, permission_str) VALUES
-- 用户管理权限
(101, 100, '查看用户', 'user:view'),
(102, 100, '用户列表', 'user:list'),
(103, 100, '添加用户', 'user:add'),
(104, 100, '更新用户', 'user:update'),
(105, 100, '删除用户', 'user:delete'),
(106, 100, '分配角色', 'user:assign:role'),

-- 角色管理权限
(200, NULL, '角色管理权限', 'role:per'),
(201, 200, '查看角色', 'role:view'),
(202, 200, '角色列表', 'role:list'),
(203, 200, '添加角色', 'role:add'),
(204, 200, '更新角色', 'role:update'),
(205, 200, '删除角色', 'role:delete'),
(206, 200, '分配权限', 'role:assign:permission'),

-- 权限管理权限
(300, NULL, '权限管理权限', 'permission:per'),
(301, 300, '查看权限', 'permission:view'),
(302, 300, '权限列表', 'permission:list'),
(303, 300, '添加权限', 'permission:add'),
(304, 300, '更新权限', 'permission:update'),
(305, 300, '删除权限', 'permission:delete');

-- 为管理员角色分配所有权限
INSERT INTO role_permission (role_id, permission_id) VALUES
-- 用户管理权限
(1, 1), (1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105), (1, 106),
-- 角色管理权限
(1, 200), (1, 201), (1, 202), (1, 203), (1, 204), (1, 205), (1, 206),
-- 权限管理权限
(1, 300), (1, 301), (1, 302), (1, 303), (1, 304), (1, 305);

-- 为普通用户角色分配基础权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 1), (2, 100), (2, 101), (2, 102);

-- 更新现有权限数据
UPDATE permission SET permission_str = 'user:list' WHERE id = 100;
UPDATE permission SET permission_str = 'user:view' WHERE id = 1;