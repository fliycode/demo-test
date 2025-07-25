package com.xm.demotest.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.Result;
import com.xm.demotest.common.annotation.RequirePermission;
import com.xm.demotest.pojo.Permission;
import com.xm.demotest.service.user.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    @RequirePermission("permission:add")
    public Result<String> addPermission(@RequestParam(required = false) Integer parentId, 
                                        @RequestParam String permissionName, 
                                        @RequestParam String permissionStr) {
        permissionService.addPermission(parentId, permissionName, permissionStr);
        return Result.success("权限添加成功");
    }

    @DeleteMapping("/delete/{permissionId}")
    @RequirePermission("permission:delete")
    public Result<String> deletePermission(@PathVariable Integer permissionId) {
        permissionService.deletePermission(permissionId);
        return Result.success("权限删除成功");
    }

    @PutMapping("/update/{permissionId}")
    @RequirePermission("permission:update")
    public Result<String> updatePermission(@PathVariable Integer permissionId, 
                                           @RequestParam String permissionName, 
                                           @RequestParam String permissionStr) {
        permissionService.updatePermission(permissionId, permissionName, permissionStr);
        return Result.success("权限更新成功");
    }

    @GetMapping("/info/{permissionId}")
    @RequirePermission("permission:view")
    public Result<Map<String, Object>> getPermissionInfo(@PathVariable Integer permissionId) {
        return Result.success(permissionService.getPermissionInfo(permissionId));
    }

    @GetMapping("/{permissionId}")
    @RequirePermission("permission:view")
    public Result<Permission> getPermissionById(@PathVariable Integer permissionId) {
        return Result.success(permissionService.getPermissionById(permissionId));
    }

    @GetMapping("/list")
    @RequirePermission("permission:list")
    public Result<List<Permission>> getAllPermissions() {
        return Result.success(permissionService.getAllPermissions());
    }

    @GetMapping("/page")
    @RequirePermission("permission:list")
    public Result<Page<Permission>> getPermissionsByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(permissionService.getPermissionsByPage(pageNum, pageSize));
    }

    @GetMapping("/tree")
    @RequirePermission("permission:list")
    public Result<List<Permission>> getPermissionTree() {
        return Result.success(permissionService.getPermissionTree());
    }

    @GetMapping("/user/{userId}")
    @RequirePermission("permission:view")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Integer userId) {
        return Result.success(permissionService.getPermissionsByUserId(userId));
    }
}
