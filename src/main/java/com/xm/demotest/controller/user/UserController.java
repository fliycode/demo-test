package com.xm.demotest.controller.user;

import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/add")
    public Map<String, String> addUser(String username, String password, String phone, String name, String status) {
        userService.addUser(username, password, phone, name, status);
        return Map.of("error_message", "用户添加成功");
    }

    @DeleteMapping("/user/delete")
    public Map<String, String> deleteUser(String username) {
        userService.deleteUser(username);
        return Map.of("error_message", "用户删除成功");
    }

    @PutMapping("/user/update")
    public Map<String, String> updateUser(String username, String password, String phone, String name, String status) {
        userService.updateUser(username, password, phone, name, status);
        return Map.of("error_message", "用户更新成功");
    }

    @GetMapping("/user/info")
    public Map<String, Object> getUserInfo(String username) {
        return userService.getUserInfo(username);
    }
}
