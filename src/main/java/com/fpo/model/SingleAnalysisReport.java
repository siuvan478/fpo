package com.fpo.model;


import java.util.ArrayList;
import java.util.List;

public class SingleAnalysisReport {

    private String title; //采购名称

    private List<OrderDetailsParam> orderDetails = new ArrayList<>(); //采购明细

    private List<QuoteParam> quoteDetails = new ArrayList<>(); //报价明细

    public SingleAnalysisReport() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<OrderDetailsParam> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailsParam> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<QuoteParam> getQuoteDetails() {
        return quoteDetails;
    }

    public void setQuoteDetails(List<QuoteParam> quoteDetails) {
        this.quoteDetails = quoteDetails;
    }
}