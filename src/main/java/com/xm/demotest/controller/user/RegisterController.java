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
    public Result<String> register(@RequestParam String username, 
                                   @RequestParam String password, 
                                   @RequestParam String phone, 
                                   @RequestParam String name) {
        String status = "1";
        Map<String, String> result = registerService.register(username, password, phone, name, status);
        
        if (result.containsKey("message")) {
            return Result.success(result.get("message"));
        } else {
            return Result.error(result.get("error_message"));
        }
    }
}
