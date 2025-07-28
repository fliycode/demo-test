package com.xm.demotest.controller;

import com.xm.demotest.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "Application is running successfully");
        return Result.success(data, "Health check passed");
    }

    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("This is a public endpoint, no authentication required");
    }
}