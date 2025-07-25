-- 创建数据库
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE demo;

-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id int NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL COMMENT '用户名',
  password varchar(100) NOT NULL COMMENT '密码',
  email varchar(100) DEFAULT NULL COMMENT '邮箱',
  phone varchar(20) DEFAULT NULL COMMENT '手机号',
  status tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  create_time timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 角色表
DROP TABLE IF EXISTS role;
CREATE TABLE role (
  id int NOT NULL AUTO_INCREMENT,
  role_name varchar(50) NOT NULL COMMENT '角色名称',
  role_key varchar(50) NOT NULL COMMENT '角色标识',
  description varchar(200) DEFAULT NULL COMMENT '角色描述',
  status tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  create_time timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 权限表
DROP TABLE IF EXISTS permission;
CREATE TABLE permission (
  id int NOT NULL AUTO_INCREMENT,
  parent_id int DEFAULT NULL COMMENT '父权限ID',
  permission_name varchar(100) NOT NULL COMMENT '权限名称',
  permission_str varchar(50) NOT NULL COMMENT '权限字符串',
  description varchar(200) DEFAULT NULL COMMENT '权限描述',
  status tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  create_time timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_permission_str (permission_str)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 用户角色关系表
DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
  user_id int NOT NULL,
  role_id int NOT NULL,
  create_time timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 角色权限关系表
DROP TABLE IF EXISTS role_permission;
CREATE TABLE role_permission (
  role_id int NOT NULL,
  permission_id int NOT NULL,
  create_time timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
  FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 插入测试数据

-- 插入用户数据 (密码为123456的MD5值)
INSERT INTO user (id, username, password, email, phone, status) VALUES 
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', '13800138000', 1),
(2, 'user', 'e10adc3949ba59abbe56e057f20f883e', 'user@example.com', '13800138001', 1);

-- 插入角色数据
INSERT INTO role (id, role_name, role_key, description, status) VALUES 
(1, '管理员', 'admin', '系统管理员，拥有所有权限', 1),
(2, '普通用户', 'user', '普通用户，拥有基础权限', 1);

-- 插入权限数据
INSERT INTO permission (id, parent_id, permission_name, permission_str, description, status) VALUES 
(1, NULL, '用户管理', 'user', '用户管理相关权限', 1),
(2, 1, '查看用户列表', 'user:list', '查看所有用户信息', 1),
(3, 1, '查看用户详情', 'user:per', '查看单个用户详细信息', 1),
(4, 1, '创建用户', 'user:create', '创建新用户', 1),
(5, 1, '更新用户', 'user:update', '更新用户信息', 1),
(6, 1, '删除用户', 'user:delete', '删除用户', 1),
(10, NULL, '系统管理', 'system', '系统管理相关权限', 1),
(11, 10, '权限管理', 'system:permission', '管理系统权限', 1),
(12, 10, '角色管理', 'system:role', '管理系统角色', 1);

-- 插入用户角色关系
INSERT INTO user_role (user_id, role_id) VALUES 
(1, 1),  -- admin用户拥有管理员角色
(2, 2);  -- user用户拥有普通用户角色

-- 插入角色权限关系
INSERT INTO role_permission (role_id, permission_id) VALUES 
-- 管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 10), (1, 11), (1, 12),
-- 普通用户只有查看权限
(2, 1), (2, 2), (2, 3);