package com.xm.demotest.common.exception;

/**
 * 权限异常类
 */
public class PermissionException extends RuntimeException {
    
    public PermissionException(String message) {
        super(message);
    }
    
    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}