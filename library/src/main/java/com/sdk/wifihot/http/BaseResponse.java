package com.sdk.wifihot.http;

public class BaseResponse<T> {
    private int code;
    private String message;
    private String status;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseReponse{" +
                "code=" + code +
                ", message=" + message +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}
