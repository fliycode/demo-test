-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `status` char(1) DEFAULT '1' COMMENT '状态 1:启用 0:禁用',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_key` varchar(50) NOT NULL UNIQUE COMMENT '角色标识',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int DEFAULT NULL COMMENT '父权限ID',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_str` varchar(100) NOT NULL UNIQUE COMMENT '权限字符串',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `fk_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 插入默认角色
INSERT IGNORE INTO role (id, role_name, role_key) VALUES
(1, '超级管理员', 'admin'),
(2, '普通用户', 'user');

-- 插入基础权限
INSERT IGNORE INTO permission (id, parent_id, permission_name, permission_str) VALUES
(1, NULL, '用户查看', 'user:view'),
(100, NULL, '用户管理权限', 'user:per');

-- 清空现有数据（可选）
DELETE FROM role_permission;
DELETE FROM user_role WHERE user_id > 1; -- 保留admin用户
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

-- 插入默认管理员用户（密码：admin123）
INSERT IGNORE INTO user (id, username, password, phone, name, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXiRowMNRKMTOJKd4vz0.qlNuxC', '13888888888', '管理员', '1');

-- 给管理员分配角色
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (1, 1);