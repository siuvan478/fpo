package com.fpo.model;

import java.io.Serializable;

public class Template implements Serializable{
    private static final long serialVersionUID = -3229858416443989062L;
    private Long id;

    private String title;

    private String filed;

    private Integer javaType;

    private Integer index;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public Integer getJavaType() {
        return javaType;
    }

    public void setJavaType(Integer javaType) {
        this.javaType = javaType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}