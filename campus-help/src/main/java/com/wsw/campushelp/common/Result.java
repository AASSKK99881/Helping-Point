package com.wsw.campushelp.common;

public class Result<T> {
    private Integer code; // 状态码：200成功，400失败
    private String msg;   // 提示信息
    private T data;       // 数据（比如用户对象）

    // 空构造
    public Result() {}

    // 全参构造
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ✅ 快捷方法：成功 (不带数据)
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // ✅ 快捷方法：成功 (带数据)
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // ❌ 快捷方法：失败
    public static <T> Result<T> error(String msg) {
        return new Result<>(400, msg, null);
    }

    // Getters and Setters (必须有，否则前端收不到数据)
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}