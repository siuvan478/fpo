package com.fpo.base;


public class ResultData<T> {

    private T data;
    private String message;
    private Integer code;

    public ResultData() {
        this.message = HttpStateEnum.OK.getDesc();
        this.code = HttpStateEnum.OK.getCode();
    }

    public ResultData(T data) {
        this.data = data;
        this.message = HttpStateEnum.OK.getDesc();
        this.code = HttpStateEnum.OK.getCode();
    }

    public ResultData(T data, String message) {
        this.data = data;
        this.message = message;
        this.code = HttpStateEnum.OK.getCode();
    }

    public ResultData(T data, Integer code) {
        this.data = data;
        this.code = code;
    }

    public ResultData(T data, String message, Integer code) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}