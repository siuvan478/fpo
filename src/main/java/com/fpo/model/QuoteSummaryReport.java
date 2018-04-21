package com.fpo.model;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 报价汇总
 */
public class QuoteSummaryReport extends Report {

    private static final long serialVersionUID = 1912246093103754142L;

    private BigDecimal minPriceGroup; //最低价组合

    private List<QuoteParam> summaryList = new ArrayList<>(); //报价汇总列表

    public QuoteSummaryReport() {
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