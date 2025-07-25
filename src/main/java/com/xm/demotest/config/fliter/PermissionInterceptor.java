package com.xm.demotest.config.fliter;

import com.xm.demotest.config.annotation.RequirePermission;
import com.xm.demotest.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 权限拦截器
 * 检查用户是否有访问特定接口的权限
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        // 检查方法上是否有权限注解
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        
        // 如果方法上没有，检查类上是否有权限注解
        if (requirePermission == null) {
            requirePermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }
        
        // 如果没有权限注解，直接放行
        if (requirePermission == null) {
            return true;
        }

        // 获取用户ID
        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error_message\":\"未登录或登录已过期\"}");
            return false;
        }

        try {
            Integer userId = Integer.parseInt(userIdStr);
            String requiredPermission = requirePermission.value();
            
            // 检查用户是否有所需权限
            if (!authService.hasPermission(userId, requiredPermission)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error_message\":\"权限不足，需要权限: " + requiredPermission + "\"}");
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error_message\":\"用户ID格式错误\"}");
            return false;
        }
    }
}