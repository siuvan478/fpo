package com.fpo.model;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 报价汇总
 */
public class QuoteSummaryReport {

    private String title; //采购单名称

    private BigDecimal minPriceGroup; //最低价组合

    private List<QuoteParam> summaryList = new ArrayList<>(); //报价汇总列表

    public QuoteSummaryReport() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getMinPriceGroup() {
        return minPriceGroup;
    }

    public void setMinPriceGroup(BigDecimal minPriceGroup) {
        this.minPriceGroup = minPriceGroup;
    }

    public List<QuoteParam> getSummaryList() {
        return summaryList;
    }

    public void setSummaryList(List<QuoteParam> summaryList) {
        this.summaryList = summaryList;
    }
}