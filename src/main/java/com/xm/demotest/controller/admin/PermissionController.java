package com.xm.demotest.controller.admin;

import com.xm.demotest.config.annotation.RequirePermission;
import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.mapper.RoleMapper;
import com.xm.demotest.pojo.Permission;
import com.xm.demotest.pojo.Role;
import com.xm.demotest.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/permission")
public class PermissionController {
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private AuthService authService;

    /**
     * 获取所有权限列表 - 需要管理员权限
     */
    @GetMapping("/list")
    @RequirePermission(value = "admin:permission:list", description = "查看权限列表")
    public Map<String, Object> getAllPermissions() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Permission> permissions = permissionMapper.selectList(null);
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
     * 获取所有角色列表
     */
    @GetMapping("/roles")
    @RequirePermission(value = "admin:role:list", description = "查看角色列表")
    public Map<String, Object> getAllRoles() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Role> roles = roleMapper.selectList(null);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", roles);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户的权限详情
     */
    @GetMapping("/user/{userId}")
    @RequirePermission(value = "admin:user:permission", description = "查看用户权限")
    public Map<String, Object> getUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Permission> permissions = permissionMapper.selectPermissionDetailsByUserId(userId);
            List<Role> roles = roleMapper.selectRolesByUserId(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("permissions", permissions);
            data.put("roles", roles);
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 刷新用户权限缓存
     */
    @PostMapping("/refresh/{userId}")
    @RequirePermission(value = "admin:permission:refresh", description = "刷新用户权限缓存")
    public Map<String, Object> refreshUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            authService.refreshUserPermissions(userId);
            result.put("code", 200);
            result.put("message", "权限缓存刷新成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "刷新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 清除用户权限缓存
     */
    @DeleteMapping("/cache/{userId}")
    @RequirePermission(value = "admin:permission:clear", description = "清除用户权限缓存")
    public Map<String, Object> clearUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            authService.clearUserPermissions(userId);
            result.put("code", 200);
            result.put("message", "权限缓存清除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "清除失败: " + e.getMessage());
        }
        return result;
    }
}