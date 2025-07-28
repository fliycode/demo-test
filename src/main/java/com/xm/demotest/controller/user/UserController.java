package com.xm.demotest.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.Result;
import com.xm.demotest.config.annotation.RequirePermission;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @RequirePermission(value = "user:add", description = "添加用户")
    public Result<String> addUser(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String phone,
                                  @RequestParam String name,
                                  @RequestParam(required = false) String status) {
        try {
            userService.addUser(username, password, phone, name, status);
            return Result.success("用户添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{username}")
    @RequirePermission(value = "user:delete", description = "删除用户")
    public Result<String> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/id/{userId}")
    @RequirePermission(value = "user:delete", description = "删除用户")
    public Result<String> deleteUserById(@PathVariable Integer userId) {
        try {
            userService.deleteUserById(userId);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    @RequirePermission(value = "user:update", description = "更新用户")
    public Result<String> updateUser(@RequestParam String username,
                                     @RequestParam(required = false) String password,
                                     @RequestParam(required = false) String phone,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String status) {
        try {
            userService.updateUser(username, password, phone, name, status);
            return Result.success("用户更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info/{username}")
    @RequirePermission(value = "user:view", description = "查看用户")
    public Result<Map<String, Object>> getUserInfo(@PathVariable String username) {
        try {
            Map<String, Object> userInfo = userService.getUserInfo(username);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info/id/{userId}")
    @RequirePermission(value = "user:view", description = "查看用户")
    public Result<Map<String, Object>> getUserInfoById(@PathVariable Integer userId) {
        try {
            Map<String, Object> userInfo = userService.getUserInfoById(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    @RequirePermission(value = "user:list", description = "用户列表")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/page")
    @RequirePermission(value = "user:list", description = "用户列表")
    public Result<Page<User>> getUsersByPage(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String keyword) {
        try {
            Page<User> userPage = userService.getUsersByPage(current, size, keyword);
            return Result.success(userPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/assignRoles")
    @RequirePermission(value = "user:assign:role", description = "分配角色")
    public Result<String> assignRoles(@RequestParam Integer userId,
                                      @RequestParam List<Integer> roleIds) {
        try {
            userService.assignRoles(userId, roleIds);
            return Result.success("角色分配成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/roles/{userId}")
    @RequirePermission(value = "user:view", description = "查看用户")
    public Result<List<Integer>> getUserRoles(@PathVariable Integer userId) {
        try {
            List<Integer> roleIds = userService.getUserRoles(userId);
            return Result.success(roleIds);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/status")
    @RequirePermission(value = "user:update", description = "更新用户")
    public Result<String> updateUserStatus(@RequestParam Integer userId,
                                           @RequestParam String status) {
        try {
            userService.updateUserStatus(userId, status);
            return Result.success("用户状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
