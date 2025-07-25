package com.xm.demotest.config.fliter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xm.demotest.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Token过期时间延长（续期）
    private static final long TOKEN_RENEWAL_TIME = 3600; // 1小时

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的Token
        String token = request.getHeader("Authorization");

        // 如果没有Token，返回401未授权
        if (token == null || token.isEmpty()) {
            log.warn("请求 {} 缺少Authorization头", request.getRequestURI());
            writeErrorResponse(response, Result.unauthorized("缺少访问令牌"));
            return false;
        }

        // 从Redis中获取用户ID
        String userId = redisTemplate.opsForValue().get(token);

        // 如果用户ID不存在，返回401未授权
        if (userId == null) {
            log.warn("无效的token: {}", token);
            writeErrorResponse(response, Result.unauthorized("访问令牌无效或已过期"));
            return false;
        }

        // Token续期
        redisTemplate.expire(token, TOKEN_RENEWAL_TIME, TimeUnit.SECONDS);

        // 将用户ID存入请求属性中，供后续处理使用
        request.setAttribute("userId", userId);
        
        log.debug("用户 {} 通过身份验证", userId);

        return true; // 继续处理请求
    }
    
    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setStatus(result.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        String jsonResponse = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResponse);
    }
}
