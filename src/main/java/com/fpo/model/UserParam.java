package com.fpo.model;


import java.io.Serializable;

public class UserParam implements Serializable {

    private static final long serialVersionUID = -4001744128001479844L;
    private String username;
    private String password;
    private String verifyCode;
    private Integer loginMode;//登录方式 1=短信登录 2=密码登录
    private Integer verifyCodeType;//短信验证码类型 1=注册 2=重置密码 3=短信登录

    public UserParam() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public Integer getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(Integer loginMode) {
        this.loginMode = loginMode;
    }

    public Integer getVerifyCodeType() {
        return verifyCodeType;
    }

    public void setVerifyCodeType(Integer verifyCodeType) {
        this.verifyCodeType = verifyCodeType;
    }
}