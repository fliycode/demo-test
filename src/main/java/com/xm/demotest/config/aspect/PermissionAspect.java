package com.xm.demotest.config.aspect;

import com.xm.demotest.common.annotation.RequirePermission;
import com.xm.demotest.common.exception.PermissionException;
import com.xm.demotest.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private AuthService authService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new PermissionException("无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 从请求属性中获取用户ID（由AuthInterceptor设置）
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            throw new PermissionException("用户未登录");
        }
        
        Integer userId = Integer.valueOf(userIdObj.toString());
        String requiredPermission = requirePermission.value();
        
        // 检查权限
        boolean hasPermission = authService.hasPermission(userId, requiredPermission);
        
        if (!hasPermission) {
            log.warn("用户 {} 尝试访问需要权限 {} 的资源，但权限不足", userId, requiredPermission);
            throw new PermissionException("权限不足，需要权限: " + requiredPermission);
        }
        
        log.info("用户 {} 通过权限校验，权限: {}", userId, requiredPermission);
        
        // 继续执行目标方法
        return joinPoint.proceed();
    }
}