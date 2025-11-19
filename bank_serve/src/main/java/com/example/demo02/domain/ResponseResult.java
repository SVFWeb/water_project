package com.example.demo02.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一响应实体类 - 简化泛型设计
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult {
    private Integer code;    // 状态码
    private Object data;     // 返回数据（使用Object类型，避免泛型问题）
    private String status;   // 状态描述
    private String message;  // 详细信息（可选）

    // 私有构造函数
    private ResponseResult(Integer code, String status, Object data, String message) {
        this.code = code;
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // 成功响应 - 无数据
    public static ResponseResult success() {
        return new ResponseResult(200, "成功", null, null);
    }

    // 成功响应 - 有数据
    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "成功", data, null);
    }

    // 成功响应 - 有数据和消息
    public static ResponseResult success(Object data, String message) {
        return new ResponseResult(200, "成功", data, message);
    }

    // 失败响应
    public static ResponseResult error(Integer code, String status) {
        return new ResponseResult(code, status, null, null);
    }

    // 失败响应 - 带消息
    public static ResponseResult error(Integer code, String status, String message) {
        return new ResponseResult(code, status, null, message);
    }

    // 通用业务失败
    public static ResponseResult businessError(String message) {
        return new ResponseResult(400, "业务错误", null, message);
    }

    // 服务器错误
    public static ResponseResult serverError(String message) {
        return new ResponseResult(500, "服务器错误", null, message);
    }

    // 未找到资源
    public static ResponseResult notFound() {
        return new ResponseResult(404, "资源未找到", null, null);
    }

    // 未授权
    public static ResponseResult unauthorized() {
        return new ResponseResult(401, "未授权", null, null);
    }

    // Getter和Setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", data=" + data +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}