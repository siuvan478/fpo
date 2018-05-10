package com.fpo.model;


import java.io.Serializable;
import java.math.BigDecimal;

public class OrderDetailsParam implements Serializable {

    private static final long serialVersionUID = -2599408419638710205L;
    private Long id;

    private Long headerId;

    private String number;

    private String name;

    private String spec;

    private String brands;

    private String description;

    private Integer quantity;

    private String unit;

    private String picture;

    //采购明细ID
    private Long orderDetailId;

    //供应单价
    private BigDecimal unitPrice;

    //供应数量
    private Integer supplyQuantity;

    //小计
    private BigDecimal subtotal;

    //附件ID
    private Long attId;

    //附件信息
    private AttachmentParam attachmentInfo;

    public OrderDetailsParam() {
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = Integer.valueOf(quantity);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = Long.valueOf(orderDetailId);
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getAttId() {
        return attId;
    }

    public void setAttId(Long attId) {
        this.attId = attId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = new BigDecimal(unitPrice);
    }

    public Integer getSupplyQuantity() {
        return supplyQuantity;
    }

    public void setSupplyQuantity(Integer supplyQuantity) {
        this.supplyQuantity = supplyQuantity;
    }

    public void setSupplyQuantity(String supplyQuantity) {
        this.supplyQuantity = Integer.valueOf(supplyQuantity);
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = new BigDecimal(subtotal);
    }

    public AttachmentParam getAttachmentInfo() {
        return attachmentInfo;
    }

    public void setAttachmentInfo(AttachmentParam attachmentInfo) {
        this.attachmentInfo = attachmentInfo;
    }
}