package com.fpo.base;


public class BaseException extends Exception {

    private static final long serialVersionUID = 1453156944114228023L;

    private Integer errorCode;

    public BaseException() {
        this("", 201);
    }

    public BaseException(String message) {
        this(message, 201);
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