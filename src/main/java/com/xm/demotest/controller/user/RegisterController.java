package com.xm.demotest.controller.user;

import com.xm.demotest.common.Result;
import com.xm.demotest.service.user.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping("/user/register")
    public Result<Map<String, String>> register(@RequestParam String username,
                                                @RequestParam String password,
                                                @RequestParam(required = false) String phone,
                                                @RequestParam(required = false) String name) {
        String status = "1"; // 默认状态为启用
        Map<String, String> result = registerService.register(username, password, phone, name, status);
        
        if (result.containsKey("error_message")) {
            return Result.error(result.get("error_message"));
        }
        
        return Result.success("注册成功", result);
    }
}
