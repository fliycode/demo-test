package com.xm.demotest.controller;

import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.utils.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 测试控制器
 * 用于演示权限系统功能
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private TestUtil testUtil;

    /**
     * 测试接口 - 无需权限
     */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello! 这是一个无需权限的测试接口");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 查看Redis中的所有数据
     */
    @GetMapping("/redis/keys")
    public Map<String, Object> showRedisKeys() {
        Map<String, Object> result = new HashMap<>();
        try {
            testUtil.printAllRedisKeys();
            result.put("message", "Redis数据已打印到控制台");
            result.put("success", true);
        } catch (Exception e) {
            result.put("message", "查看Redis数据失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    /**
     * 查看用户权限信息
     */
    @GetMapping("/user/permissions/{userId}")
    public Map<String, Object> showUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Set<String> permissions = authService.getPermissions(userId);
            result.put("userId", userId);
            result.put("permissions", permissions);
            result.put("permissionCount", permissions.size());
            result.put("success", true);
            
            // 同时打印到控制台
            testUtil.printUserPermissions(userId);
        } catch (Exception e) {
            result.put("message", "获取用户权限失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    /**
     * 检查用户是否有特定权限
     */
    @GetMapping("/user/{userId}/check/{permission}")
    public Map<String, Object> checkPermission(@PathVariable Integer userId, @PathVariable String permission) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean hasPermission = authService.hasPermission(userId, permission);
            result.put("userId", userId);
            result.put("permission", permission);
            result.put("hasPermission", hasPermission);
            result.put("success", true);
        } catch (Exception e) {
            result.put("message", "检查权限失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    /**
     * 刷新用户权限缓存
     */
    @PostMapping("/user/{userId}/refresh")
    public Map<String, Object> refreshUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            authService.refreshUserPermissions(userId);
            result.put("message", "用户权限缓存已刷新");
            result.put("userId", userId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("message", "刷新权限缓存失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    /**
     * 清除用户权限缓存
     */
    @DeleteMapping("/user/{userId}/cache")
    public Map<String, Object> clearUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            authService.clearUserPermissions(userId);
            result.put("message", "用户权限缓存已清除");
            result.put("userId", userId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("message", "清除权限缓存失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    /**
     * 获取当前登录用户信息（需要token）
     */
    @GetMapping("/current/user")
    public Map<String, Object> getCurrentUser(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = request.getHeader("Authorization");
            Integer userId = (Integer) request.getAttribute("user_id");
            
            result.put("token", token);
            result.put("userId", userId);
            result.put("message", "当前登录用户信息");
            result.put("success", true);
            
            if (userId != null) {
                Set<String> permissions = authService.getPermissions(userId);
                result.put("permissions", permissions);
            }
        } catch (Exception e) {
            result.put("message", "获取当前用户信息失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }
}