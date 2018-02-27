package com.fpo.model;


import java.io.Serializable;
import java.util.ArrayList;
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
}