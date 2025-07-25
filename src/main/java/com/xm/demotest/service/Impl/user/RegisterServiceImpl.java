package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.utils.PasswordUtil;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    // 注册接口实现


    @Override
    public Map<String, String> register(String username, String password, String phone, String name, String status) {
        Map<String, String> result = new HashMap<>();



        // 检查密码

        if (password == null || password.length() < 6) {
            // 密码不符合要求
            result.put("error_message", "密码长度不能少于6位");
            return result;
        }
        // 检查手机号（可选）
        if (phone != null && !phone.trim().isEmpty() && !phone.matches("^1[3-9]\\d{9}$")) {
            // 手机号格式不正确
            result.put("error_message", "手机号格式不正确");
            return result;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> user = userMapper.selectList(queryWrapper);

        // 检查用户名是否已存在
        if (!user.isEmpty()) {
            // 用户名已存在
            result.put("error_message", "用户名已存在");
            return result;
        }
        // 创建新用户
        String pwd = PasswordUtil.encode(password);
        User newUser = new User(
                null,
                username,
                pwd,
                phone,
                name,
                status
        );


        int rows = userMapper.insert(newUser);
        if (rows > 0) {
            // 注册成功
            result.put("message", "注册成功");
            return result;
        }
        // 注册失败
        result.put("error_message", "注册失败，请稍后再试");
        return result;
    }
}
