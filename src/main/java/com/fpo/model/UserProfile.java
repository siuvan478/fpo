package com.fpo.model;


import java.io.Serializable;

public class UserProfile implements Serializable {
    private static final long serialVersionUID = 5567589618542906175L;

    private String nikeName;
    private String position;
    private String company;

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}