package com.xm.demotest.service.auth;

import java.util.Set;

public interface AuthService {
   // 获取用户权限码
    Set<String> getPermissions(Integer id);

    // 检查是否有权限访问某资源
    boolean hasPermission(Integer id, String permission);

    // 缓存用户权限到Redis
    void cacheUserPermissions(Integer userId, Set<String> permissions);

    // 清除用户权限缓存
    void clearUserPermissionsCache(Integer userId);

    // 刷新用户权限缓存
    void refreshUserPermissionsCache(Integer userId);
}
