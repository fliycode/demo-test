package com.xm.demotest.controller.user;

import com.xm.demotest.common.Result;
import com.xm.demotest.service.user.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/user/login")
    public Result<Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = loginService.login(username, password);
        Boolean success = (Boolean) result.get("success");
        String message = (String) result.get("message");
        
        if (success) {
            return Result.success(result.get("data"), message);
        } else {
            return Result.error(message);
        }
    }

    @PostMapping("/user/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        Map<String, String> result = loginService.logout(token);
        
        Boolean success = Boolean.valueOf(result.get("success"));
        String message = result.get("message");
        
        if (success) {
            return Result.success(message);
        } else {
            return Result.error(message);
        }
    }

    @PostMapping("/user/refreshToken")
    public Result<Object> refreshToken(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        Map<String, Object> result = loginService.refreshToken(token);
        
        Boolean success = (Boolean) result.get("success");
        String message = (String) result.get("message");
        
        if (success) {
            return Result.success(result.get("data"), message);
        } else {
            return Result.error(message);
        }
    }
}
