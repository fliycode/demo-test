package com.xm.demotest.controller.user;

import com.xm.demotest.service.user.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-role")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("/assign")
    public Map<String, String> assignRoleToUser(Integer userId, Integer roleId) {
        userRoleService.assignRoleToUser(userId, roleId);
        return Map.of("error_message", "分配角色成功");
    }

    @PostMapping("/remove")
    public Map<String, String> removeRoleFromUser(Integer userId, Integer roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return Map.of("error_message", "移除角色成功");
    }

    @GetMapping("/list")
    public List<Integer> getRoleIdsByUserId(Integer userId) {
        return userRoleService.getRoleIdsByUserId(userId);
    }
}