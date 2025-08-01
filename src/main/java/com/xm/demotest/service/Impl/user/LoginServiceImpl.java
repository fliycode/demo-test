package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.utils.PasswordUtil;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
   @Autowired
   private UserMapper userMapper;
   @Autowired
   private StringRedisTemplate redisTemplate;

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
                String.valueOf((user.getId())),
                TOKEN_EXPIRATION_TIME,
                TimeUnit.SECONDS
        );
        result.put("token", token);
        return result;
    }
}
