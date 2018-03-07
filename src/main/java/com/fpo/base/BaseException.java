package com.fpo.base;


public class BaseException extends Exception {

    private static final long serialVersionUID = 1453156944114228023L;

    private Integer errorCode;

    public BaseException() {
        this(HttpStateEnum.ERROR.getDesc(), HttpStateEnum.ERROR.getCode());
    }

    public BaseException(String message) {
        this(message, HttpStateEnum.ERROR.getCode());
    }

    public BaseException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}