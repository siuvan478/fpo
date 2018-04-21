package com.fpo.base;


public enum HttpStateEnum {

    OK(200, "ok"),
    ERROR(201, "内部错误"),
    NEED_CODE(202, "需要验证码"),
    NEED_LOGIN(401, "需要登录"),
    NOT_FOUND(404, "Not Found");

    public Integer code;
    public String desc;

    HttpStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}