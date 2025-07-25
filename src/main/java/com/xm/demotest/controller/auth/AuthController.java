package com.xm.demotest.controller.auth;

import com.xm.demotest.common.Result;
import com.xm.demotest.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/permissions")
    public Result<Set<String>> getPermissions(HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getAttribute("userId").toString());
        return Result.success(authService.getPermissions(userId));
    }

    @GetMapping("/hasPermission")
    public Result<Boolean> hasPermission(HttpServletRequest request, @RequestParam String permission) {
        Integer userId = Integer.valueOf(request.getAttribute("userId").toString());
        return Result.success(authService.hasPermission(userId, permission));
    }

    @PostMapping("/refresh")
    public Result<String> refreshPermissionsCache(HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getAttribute("userId").toString());
        authService.refreshUserPermissionsCache(userId);
        return Result.success("权限缓存刷新成功");
    }
}
