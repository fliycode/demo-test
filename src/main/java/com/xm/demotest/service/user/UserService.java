package com.xm.demotest.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 增加用户
    void addUser(String username, String password, String phone, String name, String status);

    // 删除用户
    void deleteUser(String username);
    
    // 根据ID删除用户
    void deleteUserById(Integer userId);

    // 更新用户信息
    void updateUser(String username, String password, String phone, String name, String status);

    // 获取用户信息
    Map<String, Object> getUserInfo(String username);
    
    // 根据ID获取用户信息
    Map<String, Object> getUserInfoById(Integer userId);

    // 获取所有用户信息
    List<User> getAllUsers();
    
    // 分页查询用户
    Page<User> getUsersByPage(Integer current, Integer size, String keyword);

    // 分配角色给用户
    void assignRoles(Integer userId, List<Integer> roleIds);

    // 获取用户角色
    List<Integer> getUserRoles(Integer userId);

    // 更新用户状态
    void updateUserStatus(Integer userId, String status);
}
