package com.xm.demotest.controller.user;

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
    // 注册接口
    @PostMapping("/user/register")
    public Map<String, String> register(@RequestParam Map<String, String> map)
    {
        String username = map.get("username");
        String password = map.get("password");
        String phone = map.get("phone");
        String name = map.get("name");
        String status = "1";
        return registerService.register(username, password, phone, name, status);
    }
}
