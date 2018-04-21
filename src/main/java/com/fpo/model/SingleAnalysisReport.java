package com.fpo.model;


import java.util.ArrayList;
import java.util.List;

/**
 * 单项分析
 */
public class SingleAnalysisReport extends Report {

    private static final long serialVersionUID = -7260657590217817494L;

    private List<OrderDetailsParam> orderDetails = new ArrayList<>(); //采购明细

    private List<QuoteParam> quoteDetails = new ArrayList<>(); //报价明细

    public SingleAnalysisReport() {
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