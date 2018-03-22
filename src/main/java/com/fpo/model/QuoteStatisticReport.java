package com.fpo.model;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 报价统计
 */
public class QuoteStatisticReport {

    private static final String[] CHINESE_NUMBER_ARRAY = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private String title; //采购单名称

    private List<String> dynaColumnName = new ArrayList<>();//动态列名

    private List<QuoteParam> statisticList = new ArrayList<>(); //报价汇总列表

    public QuoteStatisticReport() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDynaColumnName() {
        return dynaColumnName;
    }

    public void setDynaColumnName(List<String> dynaColumnName) {
        this.dynaColumnName = dynaColumnName;
    }

    public List<QuoteParam> getStatisticList() {
        return statisticList;
    }

    public void setStatisticList(List<QuoteParam> statisticList) {
        this.statisticList = statisticList;
    }

    public QuoteStatisticReport calculateColumnName() {
        int maxQuoteCount = 0;
        if (CollectionUtils.isNotEmpty(this.getStatisticList())) {
            for (QuoteParam q : this.getStatisticList()) {
                if (q.getQuoteCount() > maxQuoteCount) {
                    maxQuoteCount = q.getQuoteCount();
                }
            }
        }

        for (int i = 1; i <= maxQuoteCount; i++) {
            if (i <= 10) {
                this.dynaColumnName.add("第" + CHINESE_NUMBER_ARRAY[i - 1] + "次报价");
            } else {
                this.dynaColumnName.add("第" + i + "次报价");
            }
        }
        return this;
    }
}
