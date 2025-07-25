package com.xm.demotest.service.auth;

import java.util.Set;

public interface AuthService {
    // 获取用户的权限码
    Set<String> getPermissions(Integer id);
    // 检查用户是否有权限访问某个资源
    boolean hasPermission(Integer id, String permission);
}
