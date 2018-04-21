package com.fpo.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 采购管理列表
 */
public class OrderMgtVO implements Serializable {
    private static final long serialVersionUID = -6931863893504389561L;

    private Long id; //采购单ID

    private String title; //采购标题

    private Integer status; //状态

    private List<QuoteHeaderVO> quoteList; //报价列表

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<QuoteHeaderVO> getQuoteList() {
        return quoteList;
    }

    public void setQuoteList(List<QuoteHeaderVO> quoteList) {
        this.quoteList = quoteList;
    }
}
