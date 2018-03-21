package com.fpo.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuoteParam implements Serializable {
    private static final long serialVersionUID = -5722075908040350419L;

    private List<QuoteDetailsParam> details = new ArrayList<>();

    private Long id;

    private Long orderHeaderId;

    private String remark;

    private String companyName;

    private String contact;

    private String contactInfo;

    // for app
    private BigDecimal totalPrice; //报价总价
    private Date quoteTime; //报价时间
    private Integer quoteCount;//报价次数
    private Double percent; //幅度
    // for order information
    private String orderTitle; //采购单名称
    private String orderCompanyName; //采购单单位
    private String orderContact; //联系人
    private String orderContactInfo; //联系方式

    public QuoteParam() {
    }

    public List<QuoteDetailsParam> getDetails() {
        return details;
    }

    public void setDetails(List<QuoteDetailsParam> details) {
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderHeaderId() {
        return orderHeaderId;
    }

    public void setOrderHeaderId(Long orderHeaderId) {
        this.orderHeaderId = orderHeaderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(Date quoteTime) {
        this.quoteTime = quoteTime;
    }

    public Integer getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(Integer quoteCount) {
        this.quoteCount = quoteCount;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getOrderCompanyName() {
        return orderCompanyName;
    }

    public void setOrderCompanyName(String orderCompanyName) {
        this.orderCompanyName = orderCompanyName;
    }

    public String getOrderContact() {
        return orderContact;
    }

    public void setOrderContact(String orderContact) {
        this.orderContact = orderContact;
    }

    public String getOrderContactInfo() {
        return orderContactInfo;
    }

    public void setOrderContactInfo(String orderContactInfo) {
        this.orderContactInfo = orderContactInfo;
    }

    public BigDecimal getExcelTotalPrice() {
        BigDecimal b = new BigDecimal(0);
        if (this.getDetails() != null && this.getDetails().size() > 0) {
            for (QuoteDetailsParam d : this.getDetails()) {
                b = b.add(d.getUnitPrice().multiply(new BigDecimal(d.getSupplyQuantity())));
            }
        }
        return b;
    }

}