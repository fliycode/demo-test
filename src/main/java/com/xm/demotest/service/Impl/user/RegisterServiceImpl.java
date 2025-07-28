package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.mapper.UserRoleMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.pojo.UserRole;
import com.xm.demotest.service.user.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Map<String, String> register(String username, String password, String phone, String name, String status) {
        Map<String, String> result = new HashMap<>();

        // 检查用户名
        if (username == null || username.trim().isEmpty()) {
            result.put("error_message", "用户名不能为空");
            return result;
        }
        
        if (username.length() < 3 || username.length() > 20) {
            result.put("error_message", "用户名长度应在3-20位之间");
            return result;
        }

        // 检查密码
        if (password == null || password.length() < 6) {
            result.put("error_message", "密码长度不能少于6位");
            return result;
        }
        
        // 检查手机号
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            result.put("error_message", "手机号格式不正确");
            return result;
        }
        
        // 检查姓名
        if (name == null || name.trim().isEmpty()) {
            result.put("error_message", "姓名不能为空");
            return result;
        }

        // 检查用户名是否已存在
        QueryWrapper<User> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", username);
        Long usernameCount = userMapper.selectCount(usernameQuery);
        if (usernameCount > 0) {
            result.put("error_message", "用户名已存在");
            return result;
        }
        
        // 检查手机号是否已存在
        QueryWrapper<User> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("phone", phone);
        Long phoneCount = userMapper.selectCount(phoneQuery);
        if (phoneCount > 0) {
            result.put("error_message", "手机号已被注册");
            return result;
        }

        try {
            // 创建新用户
            String encodedPassword = passwordEncoder.encode(password);
            User newUser = new User(
                    null,
                    username,
                    encodedPassword,
                    phone,
                    name,
                    status != null ? status : "1"
            );

            int rows = userMapper.insert(newUser);
            if (rows > 0) {
                // 分配默认角色（普通用户角色ID为2）
                UserRole userRole = new UserRole();
                userRole.setUserId(newUser.getId());
                userRole.setRoleId(2); // 默认角色ID
                userRoleMapper.insert(userRole);
                
                result.put("message", "注册成功");
                return result;
            }
        } catch (Exception e) {
            result.put("error_message", "注册失败：" + e.getMessage());
            return result;
        }
        
        result.put("error_message", "注册失败，请稍后再试");
        return result;
    }
}
