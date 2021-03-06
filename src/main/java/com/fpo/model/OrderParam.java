package com.fpo.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderParam implements Serializable {
    private static final long serialVersionUID = -8891340813965516129L;

    private List<OrderDetailsParam> details = new ArrayList<>();

    private Long id;

    private String title;

    private Integer invoiceMode;

    private String quoteMode;

    private Integer paymentMode;

    private String paymentRemark;

    private Date receiptDate;

    private String receiptAddress;

    private String remark;

    private String companyName;

    private String contact;

    private String contactInfo;

    private Long userId;

    //for app
    private String invoiceModeName;
    private String quoteModeName;
    private String paymentModeName;
    // 附件ID
    private List<Long> attIdList;
    // 附件列表
    private List<Attachment> attachmentList;

    public OrderParam() {
    }

    public List<OrderDetailsParam> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailsParam> details) {
        this.details = details;
    }

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

    public Integer getInvoiceMode() {
        return invoiceMode;
    }

    public void setInvoiceMode(Integer invoiceMode) {
        this.invoiceMode = invoiceMode;
    }

    public String getQuoteMode() {
        return quoteMode;
    }

    public void setQuoteMode(String quoteMode) {
        this.quoteMode = quoteMode;
    }

    public Integer getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Integer paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentRemark() {
        return paymentRemark;
    }

    public void setPaymentRemark(String paymentRemark) {
        this.paymentRemark = paymentRemark;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptAddress() {
        return receiptAddress;
    }

    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getInvoiceModeName() {
        return invoiceModeName;
    }

    public void setInvoiceModeName(String invoiceModeName) {
        this.invoiceModeName = invoiceModeName;
    }

    public String getQuoteModeName() {
        return quoteModeName;
    }

    public void setQuoteModeName(String quoteModeName) {
        this.quoteModeName = quoteModeName;
    }

    public String getPaymentModeName() {
        return paymentModeName;
    }

    public void setPaymentModeName(String paymentModeName) {
        this.paymentModeName = paymentModeName;
    }

    public List<Long> getAttIdList() {
        return attIdList;
    }

    public void setAttIdList(List<Long> attIdList) {
        this.attIdList = attIdList;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }
}