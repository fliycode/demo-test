package com.xm.demotest.service.Impl.auth;

import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String PERMISSION_CACHE_PREFIX = "user:permissions:";
    private static final long PERMISSION_CACHE_EXPIRE = 3600; // 1小时

    @Override
    public Set<String> getPermissions(Integer userId) {
        // 先从缓存获取
        Set<String> cachedPermissions = getCachedPermissions(userId);
        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
            return cachedPermissions;
        }

        // 从数据库获取
        List<String> permissionList = permissionMapper.selectPermissionsByUserId(userId);
        Set<String> permissions = new HashSet<>(permissionList);
        
        // 缓存权限
        cacheUserPermissions(userId, permissions);
        
        log.info("用户 {} 的权限: {}", userId, permissions);
        return permissions;
    }

    @Override
    public boolean hasPermission(Integer userId, String permission) {
        Set<String> userPermissions = getPermissions(userId);
        return userPermissions.contains(permission);
    }

    @Override
    public void cacheUserPermissions(Integer userId, Set<String> permissions) {
        String key = PERMISSION_CACHE_PREFIX + userId;
        try {
            // 删除旧缓存
            redisTemplate.delete(key);
            
            // 缓存新权限
            if (!permissions.isEmpty()) {
                redisTemplate.opsForSet().add(key, permissions.toArray(new String[0]));
                redisTemplate.expire(key, PERMISSION_CACHE_EXPIRE, TimeUnit.SECONDS);
            }
            log.info("用户 {} 权限已缓存", userId);
        } catch (Exception e) {
            log.error("缓存用户权限失败: {}", e.getMessage());
        }
    }

    @Override
    public void clearUserPermissionsCache(Integer userId) {
        String key = PERMISSION_CACHE_PREFIX + userId;
        try {
            redisTemplate.delete(key);
            log.info("用户 {} 权限缓存已清除", userId);
        } catch (Exception e) {
            log.error("清除用户权限缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public void refreshUserPermissionsCache(Integer userId) {
        // 清除旧缓存
        clearUserPermissionsCache(userId);
        
        // 重新获取并缓存权限
        getPermissions(userId);
    }

    /**
     * 从缓存获取用户权限
     */
    private Set<String> getCachedPermissions(Integer userId) {
        String key = PERMISSION_CACHE_PREFIX + userId;
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("从缓存获取用户权限失败: {}", e.getMessage());
            return null;
        }
    }
}
