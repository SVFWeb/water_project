package com.example.demo02.util;

import com.example.demo02.domain.ResponseResult;
import org.springframework.http.ResponseEntity;

/**
 * 响应工具类 - 简化设计
 */
public class ResponseUtils {

    // 成功响应
    public static ResponseEntity<ResponseResult> ok() {
        return ResponseEntity.ok(ResponseResult.success());
    }

    public static ResponseEntity<ResponseResult> ok(Object data) {
        return ResponseEntity.ok(ResponseResult.success(data));
    }

    public static ResponseEntity<ResponseResult> ok(Object data, String message) {
        return ResponseEntity.ok(ResponseResult.success(data, message));
    }

    // 失败响应
    public static ResponseEntity<ResponseResult> error(Integer code, String status) {
        return ResponseEntity.status(code).body(ResponseResult.error(code, status));
    }

    public static ResponseEntity<ResponseResult> error(Integer code, String status, String message) {
        return ResponseEntity.status(code).body(ResponseResult.error(code, status, message));
    }

    // 业务错误
    public static ResponseEntity<ResponseResult> businessError(String message) {
        return ResponseEntity.badRequest().body(ResponseResult.businessError(message));
    }

    // 服务器错误
    public static ResponseEntity<ResponseResult> serverError(String message) {
        return ResponseEntity.internalServerError().body(ResponseResult.serverError(message));
    }

    // 未找到
    public static ResponseEntity<ResponseResult> notFound() {
        return ResponseEntity.status(404).body(ResponseResult.notFound());
    }

    // 未授权
    public static ResponseEntity<ResponseResult> unauthorized() {
        return ResponseEntity.status(401).body(ResponseResult.unauthorized());
    }
}