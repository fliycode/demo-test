package com.xm.demotest.service.Impl.auth;

import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Set<String> getPermissions(Integer userId) {
        // 先从Redis缓存中获取
        Set<String> cachedPermissions = redisUtil.getUserPermissions(userId);
        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
            return cachedPermissions;
        }

        // 缓存中没有，从数据库查询
        List<String> permissionList = permissionMapper.selectPermissionsByUserId(userId);
        Set<String> permissions = new HashSet<>(permissionList);

        // 缓存到Redis
        if (!permissions.isEmpty()) {
            cacheUserPermissions(userId, permissions);
        }

        return permissions;
    }

    @Override
    public boolean hasPermission(Integer userId, String permission) {
        // 先从Redis缓存中检查
        Boolean cachedResult = redisUtil.hasPermission(userId, permission);
        if (cachedResult != null) {
            return cachedResult;
        }

        // 缓存中没有，获取所有权限再检查
        Set<String> permissions = getPermissions(userId);
        return permissions.contains(permission);
    }

    @Override
    public void cacheUserPermissions(Integer userId, Set<String> permissions) {
        redisUtil.cacheUserPermissions(userId, permissions);
    }

    @Override
    public void clearUserPermissionsCache(Integer userId) {
        redisUtil.clearUserPermissions(userId);
    }

    @Override
    public void refreshUserPermissionsCache(Integer userId) {
        // 清除缓存
        clearUserPermissionsCache(userId);
        
        // 重新获取权限（会自动缓存）
        getPermissions(userId);
    }
}
