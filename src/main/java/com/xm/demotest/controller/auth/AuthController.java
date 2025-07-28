package com.xm.demotest.controller.auth;

import com.xm.demotest.common.Result;
import com.xm.demotest.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 获取用户权限码
     * @param request
     * @return
     */
    @GetMapping("/auth/permissions")
    public Result<Set<String>> getPermissions(HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getAttribute("userId").toString());
        return Result.success(authService.getPermissions(userId));
    }

    /**
     *  检查用户是否有某个权限
     * @param request
     * @param permission
     * @return
     */
    @GetMapping("/auth/hasPermission")
    public Result<Boolean> hasPermission(HttpServletRequest request, @RequestParam String permission) {
        Integer userId = Integer.valueOf(request.getAttribute("userId").toString());
        return Result.success(authService.hasPermission(userId, permission));
    }

    /**
     *  刷新用户权限缓存
     */
    @PostMapping("/auth/refresh")
    public Result<String> refreshPermissionsCache(HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getAttribute("userId").toString());
        authService.refreshUserPermissionsCache(userId);
        return Result.success("权限缓存刷新成功");
    }
}
