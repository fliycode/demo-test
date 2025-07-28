package com.xm.demotest.config.fliter;

import com.xm.demotest.utils.JwtUtil;
import com.xm.demotest.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // 跳过登录、注册和测试接口
        if (requestPath.equals("/user/login") || 
            requestPath.equals("/user/register") || 
            requestPath.startsWith("/test/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(tokenHeader);
        
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(tokenPrefix)) {
            String token = authHeader.substring(tokenPrefix.length()).trim();
            
            try {
                // 验证token格式和是否过期
                if (jwtUtil.validateToken(token)) {
                    // 从token中获取用户信息
                    Integer userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    
                    // 验证token在Redis中是否存在
                    Integer cachedUserId = redisUtil.getTokenUserId(token);
                    if (cachedUserId != null && cachedUserId.equals(userId)) {
                        // 设置用户信息到request属性中
                        request.setAttribute("userId", userId);
                        request.setAttribute("username", username);
                        request.setAttribute("token", token);
                    } else {
                        // token在Redis中不存在或用户ID不匹配
                        sendUnauthorizedResponse(response, "Token invalid or expired");
                        return;
                    }
                } else {
                    sendUnauthorizedResponse(response, "Token invalid or expired");
                    return;
                }
            } catch (Exception e) {
                sendUnauthorizedResponse(response, "Token parsing error");
                return;
            }
        } else {
            sendUnauthorizedResponse(response, "Authorization header missing or invalid");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
    }
}