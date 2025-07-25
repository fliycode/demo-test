package com.xm.demotest.config.fliter;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermissionRequired {
    String value(); // 权限字符串
}