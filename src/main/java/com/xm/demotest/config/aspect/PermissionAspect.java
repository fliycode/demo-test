package com.xm.demotest.config.aspect;

import com.xm.demotest.config.annotation.RequirePermission;
import com.xm.demotest.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private AuthService authService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint point, RequirePermission requirePermission) throws Throwable {
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("用户未认证");
        }

        // 获取用户ID
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new AccessDeniedException("无法获取请求信息");
        }
        
        HttpServletRequest request = attributes.getRequest();
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            throw new AccessDeniedException("无法获取用户信息");
        }

        Integer userId = (Integer) userIdObj;
        String requiredPermission = requirePermission.value();

        // 检查权限
        boolean hasPermission = authService.hasPermission(userId, requiredPermission);
        if (!hasPermission) {
            throw new AccessDeniedException("权限不足，需要权限: " + requiredPermission);
        }

        // 权限验证通过，继续执行
        return point.proceed();
    }
}