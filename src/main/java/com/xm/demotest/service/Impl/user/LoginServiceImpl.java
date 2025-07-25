package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.utils.PasswordUtil;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.LoginService;
import com.xm.demotest.service.Impl.auth.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {
   @Autowired
   private UserMapper userMapper;
   @Autowired
   private StringRedisTemplate redisTemplate;
   @Autowired
   private AuthServiceImpl authService;

   // Token过期时间
    private static final long TOKEN_EXPIRATION_TIME = 3600; // 1小时

    @Override
    public Map<String, String> getToken(String username, String password) {
        Map<String, String> result = new HashMap<>();
        // 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if(user == null) {
            result.put("error_message", "用户不存在");
            return result;
        }

        // 验证密码
        if(!PasswordUtil.matches(password, user.getPassword())) {
            result.put("error_message", "密码错误");
            return result;
        }

        // 生成Token
        String token = UUID.randomUUID().toString().replace("-","");

        // 存储到Redis中 (key: token, value: id)
        redisTemplate.opsForValue().set(
                token,
                user.getId().toString(),
                TOKEN_EXPIRATION_TIME
        );
        
        // 缓存用户权限到Redis
        authService.refreshUserPermissions(user.getId());
        
        result.put("token", token);
        result.put("user_id", user.getId().toString());
        return result;
    }
}
