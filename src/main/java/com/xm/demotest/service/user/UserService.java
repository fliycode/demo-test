package com.xm.demotest.service.user;

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
}
