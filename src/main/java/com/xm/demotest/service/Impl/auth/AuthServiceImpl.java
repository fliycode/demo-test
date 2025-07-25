package com.xm.demotest.service.Impl.auth;

import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis缓存过期时间(24小时)
    private static final long CACHE_EXPIRATION_TIME = 24 * 60 * 60;

    @Override
    public Set<String> getPermissions(Integer id) {
        // 从Redis中获取权限码
        String key = "user:" + id + ":permissions";
        Set<String> permissions = redisTemplate.opsForSet().members(key);

        // 如果Redis中没有，从数据库查询并缓存
        if (permissions == null || permissions.isEmpty()) {
            List<String> permissionList = permissionMapper.selectPermissionsByUserId(id);
            if (permissionList != null && !permissionList.isEmpty()) {
                permissions = new HashSet<>(permissionList);
                // 缓存权限到Redis
                cacheUserPermissions(id, permissions);
            } else {
                permissions = new HashSet<>();
            }
        }

        return permissions;
    }

    @Override
    public boolean hasPermission(Integer id, String permission) {
        Set<String> permissions = getPermissions(id);
        return permissions != null && permissions.contains(permission);
    }

    /**
     * 缓存用户权限到Redis
     */
    public void cacheUserPermissions(Integer userId, Set<String> permissions) {
        String key = "user:" + userId + ":permissions";
        if (permissions != null && !permissions.isEmpty()) {
            // 清除旧的权限缓存
            redisTemplate.delete(key);
            // 添加新的权限
            redisTemplate.opsForSet().add(key, permissions.toArray(new String[0]));
            // 设置过期时间
            redisTemplate.expire(key, CACHE_EXPIRATION_TIME, TimeUnit.SECONDS);
        }
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserPermissions(Integer userId) {
        String key = "user:" + userId + ":permissions";
        redisTemplate.delete(key);
    }

    /**
     * 刷新用户权限缓存
     */
    public void refreshUserPermissions(Integer userId) {
        // 先清除缓存
        clearUserPermissions(userId);
        // 重新获取权限(会自动缓存)
        getPermissions(userId);
    }
}
