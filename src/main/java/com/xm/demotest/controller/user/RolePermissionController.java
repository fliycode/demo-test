package com.xm.demotest.controller.user;

import com.xm.demotest.service.user.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role-permission")
public class RolePermissionController {
    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/assign")
    public Map<String, String> assignPermissionToRole(Integer roleId, Integer permissionId) {
        rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return Map.of("error_message", "分配权限成功");
    }

    @PostMapping("/remove")
    public Map<String, String> removePermissionFromRole(Integer roleId, Integer permissionId) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return Map.of("error_message", "移除权限成功");
    }

    @GetMapping("/list")
    public List<Integer> getPermissionIdsByRoleId(Integer roleId) {
        return rolePermissionService.getPermissionIdsByRoleId(roleId);
    }
}