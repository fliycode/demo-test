package com.xm.demotest.controller.user;

import com.xm.demotest.common.Result;
import com.xm.demotest.service.user.LoginService;
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
    public Result<Map<String, String>> login(@RequestParam String username, 
                                             @RequestParam String password) {
        Map<String, String> result = loginService.getToken(username, password);
        
        if (result.containsKey("error_message")) {
            return Result.error(result.get("error_message"));
        }
        
        return Result.success("登录成功", result);
    }
}
