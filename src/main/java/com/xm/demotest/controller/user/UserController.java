package com.xm.demotest.controller.user;


import com.xm.demotest.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/add")
    public Map<String,String> addUser(@RequestParam Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        String username = map.get("username");
        String password = map.get("password");
        String phone = map.get("phone");
        String name = map.get("name");
        String status = map.get("status");
        try {
            userService.addUser(username, password, phone, name, status);
            result.put("error_message", "用户添加成功");
        } catch (Exception e) {
            result.put("error_message", e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    @DeleteMapping("/user/delete")
    public Map<String,String> deleteUser(String username) {
        Map<String, String> result = new HashMap<>();
        try {
            userService.deleteUser(username);
            result.put("error_message", "用户删除成功");
        } catch (Exception e) {
            result.put("error_message", e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    @PutMapping("/user/update")
    public Map<String, String> updateUser(@RequestParam Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        String username = map.get("username");
        String password = map.get("password");
        String phone = map.get("phone");
        String name = map.get("name");
        String status = map.get("status");

        try {
            userService.updateUser(username, password, phone, name, status);
            result.put("error_message", "用户更新成功");
        } catch (Exception e) {
            result.put("error_message", e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    @GetMapping("/user/info/{userId}")
    public Map<String, Object> getUserInfo(String username) {
        return userService.getUserInfo(username);
    }


}
