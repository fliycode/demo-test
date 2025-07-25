package com.xm.demotest.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 需要的权限字符串
     */
    String value();
    
    /**
     * 权限描述
     */
    String description() default "";
}