package com.xm.demotest.config.fliter;

import com.xm.demotest.mapper.PermissionMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.util.AntPathMatcher;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PermissionMapper permissionMapper;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的Token
        String token = request.getHeader("Authorization");

        // 如果没有Token，返回401未授权
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 从Redis中获取用户ID
        String userId = redisTemplate.opsForValue().get(token);

        // 如果用户ID不存在，返回401未授权
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 将用户ID存入请求属性中，供后续处理使用
        request.setAttribute("userId", userId);

        // 权限校验
        // 获取用户所有权限字符串
        java.util.List<String> permissions = permissionMapper.selectPermissionsByUserId(Integer.valueOf(userId));
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        // 这里假设权限字符串和接口路径一一对应，或可自定义映射
        boolean hasPermission = permissions.stream().anyMatch(p -> pathMatcher.match(p, requestURI));
        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true; // 继续处理请求
    }
}
