package com.fpo.model;

import java.io.Serializable;

/**
 * 报表
 */
public class Report implements Serializable {
    private static final long serialVersionUID = -5346258154349926490L;

    private String title; //采购单名称

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
