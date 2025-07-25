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
        // 从Redis中获取权限码
        String key = "user:" + id + ":permissions";
        Set<String> permissions = redisTemplate.opsForSet().members(key);




        return permissions;
    }

    @Override
    public boolean hasPermission(Integer id, String permission) {
        // 从Redis中获取权限码
        String key = "user:" + id + ":permissions";
        Set<String> permissions = redisTemplate.opsForSet().members(key);

        // 检查是否包含指定的权限码
        if (permissions != null && permissions.contains(permission)) {
            return true;
        }
        return false;
    }
}
