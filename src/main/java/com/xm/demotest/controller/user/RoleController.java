package com.xm.demotest.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.RoleMapper;
import com.xm.demotest.pojo.Role;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RoleController  {
    @Autowired
    private RoleService roleService;

    @PostMapping("/role/add")
    public Map<String, String> addRole(@RequestParam Map<String, String> map) {
        String roleName = map.get("roleName");
        String description = map.get("description");
        roleService.addRole(roleName, description);
        return Map.of("error_message","角色添加成功");
    }
    @DeleteMapping("/role/delete")
    public Map<String, String> deleteRole(Integer roleId) {
        roleService.deleteRole(roleId);
        return Map.of("error_message","角色删除成功");
    }
    @PutMapping("/role/update")
    public Map<String, String> updateRole(@RequestParam Map<String, String> map) {
        Integer roleId = Integer.valueOf(map.get("roleId"));
        String roleName = map.get("roleName");
        String roleKey = map.get("roleKey");
        roleService.updateRole(roleId, roleName, roleKey);
        return Map.of("error_message", "角色更新成功");
    }
    @GetMapping("/role/info")
    public Map<String, Object> getRoleInfo(Integer roleId) {
        return roleService.getRoleInfo(roleId);
    }
}
