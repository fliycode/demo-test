package com.xm.demotest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 测试工具类
 * 用于调试和测试权限功能
 */
@Component
public class TestUtil {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 打印Redis中所有的Key
     */
    public void printAllRedisKeys() {
        Set<String> keys = redisTemplate.keys("*");
        System.out.println("Redis中的所有Key:");
        if (keys != null) {
            for (String key : keys) {
                System.out.println("Key: " + key + ", Value: " + redisTemplate.opsForValue().get(key));
            }
        }
    }

    /**
     * 打印用户的权限信息
     */
    public void printUserPermissions(Integer userId) {
        String key = "user:" + userId + ":permissions";
        Set<String> permissions = redisTemplate.opsForSet().members(key);
        System.out.println("用户" + userId + "的权限:");
        if (permissions != null) {
            for (String permission : permissions) {
                System.out.println("- " + permission);
            }
        } else {
            System.out.println("该用户没有权限或权限未缓存");
        }
    }

    /**
     * 清除所有Redis缓存
     */
    public void clearAllRedisCache() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("已清除" + keys.size() + "个Redis缓存");
        }
    }
}