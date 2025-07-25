package com.xm.demotest.config.fliter;

import com.xm.demotest.mapper.PermissionMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {
    @Autowired
    private PermissionMapper permissionMapper;

    @Around("@annotation(com.xm.demotest.config.fliter.PermissionRequired)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PermissionRequired permissionRequired = method.getAnnotation(PermissionRequired.class);
        String required = permissionRequired.value();
        List<String> permissions = permissionMapper.selectPermissionsByUserId(userId);
        if (!permissions.contains(required)) {
            throw new RuntimeException("无权限");
        }
        return joinPoint.proceed();
    }
}