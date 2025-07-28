package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.utils.JwtUtil;
import com.xm.demotest.utils.PasswordUtil;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.LoginService;
import com.xm.demotest.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthService authService;

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 验证用户状态
        if (!"1".equals(user.getStatus())) {
            result.put("success", false);
            result.put("message", "用户已被禁用");
            return result;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 将token存储到Redis中
        redisUtil.cacheToken(token, user.getId());

        // 获取用户权限并缓存
        Set<String> permissions = authService.getPermissions(user.getId());

        // 返回登录成功信息
        result.put("success", true);
        result.put("message", "登录成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("name", user.getName());
        data.put("permissions", permissions);
        
        result.put("data", data);
        return result;
    }

    @Override
    public Map<String, String> logout(String token) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // 从Redis中删除token
            redisUtil.deleteToken(token);
            
            // 获取用户ID并清除权限缓存
            Integer userId = jwtUtil.getUserIdFromToken(token);
            if (userId != null) {
                authService.clearUserPermissionsCache(userId);
            }
            
            result.put("success", "true");
            result.put("message", "退出登录成功");
        } catch (Exception e) {
            result.put("success", "false");
            result.put("message", "退出登录失败");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> refreshToken(String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证旧token
            if (!jwtUtil.validateToken(token)) {
                result.put("success", false);
                result.put("message", "Token已过期或无效");
                return result;
            }
            
            // 生成新token
            String newToken = jwtUtil.refreshToken(token);
            if (newToken == null) {
                result.put("success", false);
                result.put("message", "Token刷新失败");
                return result;
            }
            
            // 获取用户ID
            Integer userId = jwtUtil.getUserIdFromToken(token);
            
            // 删除旧token，存储新token
            redisUtil.deleteToken(token);
            redisUtil.cacheToken(newToken, userId);
            
            result.put("success", true);
            result.put("message", "Token刷新成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", newToken);
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Token刷新失败: " + e.getMessage());
        }
        
        return result;
    }
}
