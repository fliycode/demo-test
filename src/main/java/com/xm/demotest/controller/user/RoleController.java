package com.xm.demotest.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.Result;
import com.xm.demotest.common.annotation.RequirePermission;
import com.xm.demotest.pojo.Role;
import com.xm.demotest.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    @RequirePermission("role:add")
    public Result<String> addRole(@RequestParam String roleName, @RequestParam String roleKey) {
        roleService.addRole(roleName, roleKey);
        return Result.success("角色添加成功");
    }

    @DeleteMapping("/delete/{roleId}")
    @RequirePermission("role:delete")
    public Result<String> deleteRole(@PathVariable Integer roleId) {
        roleService.deleteRole(roleId);
        return Result.success("角色删除成功");
    }

    @PutMapping("/update/{roleId}")
    @RequirePermission("role:update")
    public Result<String> updateRole(@PathVariable Integer roleId, 
                                     @RequestParam String roleName, 
                                     @RequestParam String roleKey) {
        roleService.updateRole(roleId, roleName, roleKey);
        return Result.success("角色更新成功");
    }

    @GetMapping("/info/{roleId}")
    @RequirePermission("role:view")
    public Result<Map<String, Object>> getRoleInfo(@PathVariable Integer roleId) {
        return Result.success(roleService.getRoleInfo(roleId));
    }

    @GetMapping("/{roleId}")
    @RequirePermission("role:view")
    public Result<Role> getRoleById(@PathVariable Integer roleId) {
        return Result.success(roleService.getRoleById(roleId));
    }

    @GetMapping("/list")
    @RequirePermission("role:list")
    public Result<List<Role>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }

    @GetMapping("/page")
    @RequirePermission("role:list")
    public Result<Page<Role>> getRolesByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(roleService.getRolesByPage(pageNum, pageSize));
    }

    @PostMapping("/{roleId}/permissions")
    @RequirePermission("role:assign:permission")
    public Result<String> assignPermissions(@PathVariable Integer roleId, 
                                            @RequestBody List<Integer> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success("权限分配成功");
    }

    @GetMapping("/{roleId}/permissions")
    @RequirePermission("role:view")
    public Result<List<Integer>> getRolePermissions(@PathVariable Integer roleId) {
        return Result.success(roleService.getRolePermissions(roleId));
    }

    @GetMapping("/user/{userId}")
    @RequirePermission("role:view")
    public Result<List<Role>> getRolesByUserId(@PathVariable Integer userId) {
        return Result.success(roleService.getRolesByUserId(userId));
    }
}
