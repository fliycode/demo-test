package com.xm.demotest.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.Result;
import com.xm.demotest.common.annotation.RequirePermission;
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
    @RequirePermission("user:add")
    public Result<String> addUser(@RequestParam String username, 
                                  @RequestParam String password, 
                                  @RequestParam(required = false) String phone, 
                                  @RequestParam(required = false) String name, 
                                  @RequestParam String status) {
        userService.addUser(username, password, phone, name, status);
        return Result.success("用户添加成功");
    }

    @DeleteMapping("/delete")
    @RequirePermission("user:delete")
    public Result<String> deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return Result.success("用户删除成功");
    }

    @DeleteMapping("/delete/{userId}")
    @RequirePermission("user:delete")
    public Result<String> deleteUserById(@PathVariable Integer userId) {
        userService.deleteUserById(userId);
        return Result.success("用户删除成功");
    }

    @PutMapping("/update")
    @RequirePermission("user:update")
    public Result<String> updateUser(@RequestParam String username, 
                                     @RequestParam(required = false) String password, 
                                     @RequestParam(required = false) String phone, 
                                     @RequestParam(required = false) String name, 
                                     @RequestParam(required = false) String status) {
        userService.updateUser(username, password, phone, name, status);
        return Result.success("用户更新成功");
    }

    @GetMapping("/info")
    @RequirePermission("user:view")
    public Result<Map<String, Object>> getUserInfo(@RequestParam String username) {
        return Result.success(userService.getUserInfo(username));
    }

    @GetMapping("/info/{userId}")
    @RequirePermission("user:view")
    public Result<User> getUserById(@PathVariable Integer userId) {
        return Result.success(userService.getUserById(userId));
    }

    @GetMapping("/list")
    @RequirePermission("user:list")
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    @GetMapping("/page")
    @RequirePermission("user:list")
    public Result<Page<User>> getUsersByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.getUsersByPage(pageNum, pageSize));
    }

    @PostMapping("/{userId}/roles")
    @RequirePermission("user:assign:role")
    public Result<String> assignRoles(@PathVariable Integer userId, 
                                      @RequestBody List<Integer> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.success("角色分配成功");
    }

    @GetMapping("/{userId}/roles")
    @RequirePermission("user:view")
    public Result<List<Integer>> getUserRoles(@PathVariable Integer userId) {
        return Result.success(userService.getUserRoles(userId));
    }

    @PutMapping("/{userId}/status")
    @RequirePermission("user:update")
    public Result<String> updateUserStatus(@PathVariable Integer userId, 
                                           @RequestParam String status) {
        userService.updateUserStatus(userId, status);
        return Result.success("用户状态更新成功");
    }
}
