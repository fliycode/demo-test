package com.xm.demotest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 默认过期时间（秒）
    private static final long DEFAULT_EXPIRE = 86400; // 24小时

    /**
     * 设置值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值和过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置值和过期时间（秒）
     */
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断key是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间（秒）
     */
    public Boolean expire(String key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * Set操作 - 添加成员
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * Set操作 - 获取所有成员
     */
    @SuppressWarnings("unchecked")
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * Set操作 - 判断是否为成员
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * Set操作 - 删除成员
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 缓存用户权限
     */
    public void cacheUserPermissions(Integer userId, Set<String> permissions) {
        String key = "user:permissions:" + userId;
        delete(key);
        if (permissions != null && !permissions.isEmpty()) {
            sAdd(key, permissions.toArray());
            expire(key, DEFAULT_EXPIRE);
        }
    }

    /**
     * 获取用户权限
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUserPermissions(Integer userId) {
        String key = "user:permissions:" + userId;
        Set<Object> permissions = sMembers(key);
        if (permissions != null) {
            return (Set<String>) (Set<?>) permissions;
        }
        return null;
    }

    /**
     * 检查用户是否有某权限
     */
    public Boolean hasPermission(Integer userId, String permission) {
        String key = "user:permissions:" + userId;
        return sIsMember(key, permission);
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserPermissions(Integer userId) {
        String key = "user:permissions:" + userId;
        delete(key);
    }

    /**
     * 缓存token
     */
    public void cacheToken(String token, Integer userId) {
        String key = "token:" + token;
        set(key, userId, DEFAULT_EXPIRE);
    }

    /**
     * 验证token
     */
    public Integer getTokenUserId(String token) {
        String key = "token:" + token;
        Object userId = get(key);
        return userId != null ? (Integer) userId : null;
    }

    /**
     * 删除token
     */
    public void deleteToken(String token) {
        String key = "token:" + token;
        delete(key);
    }
}