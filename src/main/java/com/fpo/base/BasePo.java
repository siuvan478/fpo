package com.fpo.base;

import java.io.Serializable;

public class BasePo implements Serializable {
    private static final long serialVersionUID = -5556025899088246477L;

    private String orderBy;
    private String orderColumn;

    public BasePo() {
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }
}
