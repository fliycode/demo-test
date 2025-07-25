package com.xm.demotest.service.Impl.auth;

import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public Set<String> getPermissions(Integer id) {
        return Set.of();
    }

    @Override
    public boolean hasPermission(Integer id, String permission) {
        return false;
    }

    @Override
    public void cacheUserPermissions(Integer userId, Set<String> permissions) {

    }

    @Override
    public void clearUserPermissionsCache(Integer userId) {

    }

    @Override
    public void refreshUserPermissionsCache(Integer userId) {

    }
}
