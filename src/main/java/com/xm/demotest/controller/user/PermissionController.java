package com.xm.demotest.controller.user;

import com.xm.demotest.service.user.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    // 添加权限
    @PostMapping("/permission/add")
    public Map<String, String> addPermission(Map<String, String> map)
    {

        Integer parentId = Integer.valueOf(map.get("parentId"));
        String permissionName = map.get("permissionName");
        String permissionStr = map.get("permissionStr");
        permissionService.addPermission(parentId, permissionName, permissionStr);
        return Map.of("error_message", "权限添加成功");
    }

    // 删除权限
    @DeleteMapping("/permission/delete")
    public Map<String, String> deletePermission(Integer permissionId)
    {
        permissionService.deletePermission(permissionId);
        return Map.of("error_message", "权限删除成功");
    }

    // 更新权限
    @PutMapping("/permission/update")
    public Map<String, String> updatePermission(@RequestParam Map<String, String> map)
    {
        Integer permissionId = Integer.valueOf(map.get("permissionId"));
        String permissionName = map.get("permissionName");
        String permissionStr = map.get("permissionStr");
        permissionService.updatePermission(permissionId, permissionName, permissionStr);
        return Map.of("error_message", "权限更新成功");
    }

    // 获取权限信息
    @GetMapping("/permission/info")
    Map<String, Object> getPermissionInfo(Integer permissionId) {
        return permissionService.getPermissionInfo(permissionId);
    }
}
