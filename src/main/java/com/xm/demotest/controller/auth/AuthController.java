package com.xm.demotest.controller.auth;

import com.xm.demotest.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/auth/permissions")
    public Set<String> getPermissions(Integer id) {
        return authService.getPermissions(id);
    }

    @GetMapping("/auth/hasPermission")
    public boolean hasPermission(Integer id, String permission) {
        return authService.hasPermission(id, permission);
    }
}
