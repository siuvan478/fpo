package com.fpo.model;


import java.io.Serializable;

public class UserParam implements Serializable {

    private static final long serialVersionUID = -4001744128001479844L;
    private String username;
    private String password;

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
}