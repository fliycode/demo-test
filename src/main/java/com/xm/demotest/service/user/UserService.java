package com.xm.demotest.service.user;

import com.xm.demotest.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 增加用户
    void addUser(String username, String password, String phone, String name, String status);

    // 删除用户
    void deleteUser(String username);

    // 更新用户信息
    void updateUser(String username, String password, String phone, String name, String status);

    // 获取用户信息
    Map<String, Object> getUserInfo(String username);

    // 获取所有用户信息
    List<User> getAllUsers();

    // 分配角色给用户
    void assignRoles(Integer userId, List<Integer> roleIds);

    // 获取用户角色
    List<Integer> getUserRoles(Integer userId);

    // 更新用户状态
    void updateUserStatus(Integer userId, String status);
}
