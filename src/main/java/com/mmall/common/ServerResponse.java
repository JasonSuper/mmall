package com.mmall.common;




import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * @Author Jason
 * Create in 2018-06-04 6:12
 */
// 如果序列化时，属性为null，则不进行序列化
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private int status;

    private String msg;

    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return status == ResponseEnum.SUCCESS.getKey();
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse createBySuccess() {
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getKey());
    }

    public static <T> ServerResponse createBySuccessMsg(String msg) {
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getKey(), msg);
    }

    public static <T> ServerResponse createBySuccess(T data) {
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getKey(), data);
    }

    public static <T> ServerResponse createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getKey(), msg, data);
    }

    public static <T> ServerResponse createByError() {
        return new ServerResponse<T>(ResponseEnum.ERROR.getKey());
    }

    public static <T> ServerResponse createByErrorMsg(String msg) {
        return new ServerResponse<T>(ResponseEnum.ERROR.getKey(), msg);
    }

    public static <T> ServerResponse createByErrorCodeMsg(int errCode, String errMsg) {
        return new ServerResponse<T>(errCode, errMsg);
    }
}
