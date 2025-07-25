package com.xm.demotest.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 私有构造方法
    private Result() {}

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功返回
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 失败返回
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 未授权
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权访问", null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    // 权限不足
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "权限不足", null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null);
    }
}