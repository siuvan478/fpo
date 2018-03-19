package com.fpo.model;


import java.io.Serializable;
import java.math.BigDecimal;

public class QuoteDetailsParam implements Serializable {
    private static final long serialVersionUID = 2046450457560339967L;

    private Long orderDetailId;

    private BigDecimal unitPrice;

    private Integer supplyQuantity;

    public QuoteDetailsParam() {
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getSupplyQuantity() {
        return supplyQuantity;
    }

    public void setSupplyQuantity(Integer supplyQuantity) {
        this.supplyQuantity = supplyQuantity;
    }
}