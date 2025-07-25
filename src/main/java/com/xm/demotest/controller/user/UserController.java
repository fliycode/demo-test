package com.xm.demotest.controller.user;

import com.xm.demotest.config.annotation.RequirePermission;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AuthService authService;

    /**
     * 获取所有用户信息 - 需要user:list权限
     */
    @GetMapping("/list")
    @RequirePermission(value = "user:list", description = "获取所有用户信息")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userMapper.selectList(null);
            // 隐藏密码信息
            users.forEach(user -> user.setPassword("******"));
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", users);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户详情 - 需要user:per权限
     */
    @GetMapping("/detail/{id}")
    @RequirePermission(value = "user:per", description = "用户信息权限")
    public Map<String, Object> getUserDetail(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(id);
            if (user != null) {
                user.setPassword("******"); // 隐藏密码
                result.put("code", 200);
                result.put("message", "获取成功");
                result.put("data", user);
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取当前用户的权限信息
     */
    @GetMapping("/permissions")
    public Map<String, Object> getCurrentUserPermissions(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String userIdStr = (String) request.getAttribute("userId");
            Integer userId = Integer.parseInt(userIdStr);
            
            Set<String> permissions = authService.getPermissions(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", permissions);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Map<String, Object> getCurrentUserInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String userIdStr = (String) request.getAttribute("userId");
            Integer userId = Integer.parseInt(userIdStr);
            
            User user = userMapper.selectById(userId);
            if (user != null) {
                user.setPassword("******"); // 隐藏密码
                result.put("code", 200);
                result.put("message", "获取成功");
                result.put("data", user);
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
}