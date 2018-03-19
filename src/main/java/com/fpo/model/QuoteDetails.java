package com.fpo.model;

import java.math.BigDecimal;
import java.util.Date;

public class QuoteDetails {
    private Long id;

    private Long headerId;

    private Long orderDetailId;

    private BigDecimal unitPrice;

    private Integer supplyQuantity;

    private Integer status;

    private Date createDate;

    private Date updateDate;

    public QuoteDetails(Long headerId, Integer status, Date createDate, Date updateDate) {
        this.headerId = headerId;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}