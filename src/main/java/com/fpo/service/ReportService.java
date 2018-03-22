package com.fpo.service;

import com.fpo.mapper.QuoteHeaderMapper;
import com.fpo.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ReportService {

    @Resource
    private OrderService orderService;

    @Resource
    private QuoteService quoteService;

    @Resource
    private QuoteHeaderMapper quoteHeaderMapper;

    /**
     * 获取报价汇总报表
     *
     * @param orderId 采购单ID
     * @return
     * @throws Exception
     */
    public QuoteSummaryReport getQuoteSummaryReport(Long orderId) throws Exception {
        final QuoteSummaryReport report = new QuoteSummaryReport();
        OrderHeader orderHeader = orderService.getOrderHeader(orderId);
        if (orderHeader != null) {
            report.setTitle(orderHeader.getTitle());
            report.setMinPriceGroup(quoteService.getMinPriceGroup(orderId));
            QuoteHeader condition = new QuoteHeader();
            condition.setOrderHeaderId(orderId);
            List<QuoteParam> list = quoteHeaderMapper.queryByCondition(condition);
            if (CollectionUtils.isNotEmpty(list)) {
                report.getSummaryList().addAll(list);
            }
        }
        return report;
    }

    /**
     * 获取单项分析报表
     *
     * @param orderId 采购单ID
     * @return
     * @throws Exception
     */
    public SingleAnalysisReport getSingleAnalysisReport(Long orderId) throws Exception {
        final SingleAnalysisReport report = new SingleAnalysisReport();
        OrderParam orderInfo = orderService.getOrderInfo(orderId);
        if (orderInfo != null) {
            report.setTitle(orderInfo.getTitle());
            report.getOrderDetails().addAll(orderInfo.getDetails());
        }
        List<QuoteParam> quoteInfoList = quoteService.getQuoteInfoList(orderId);
        if (CollectionUtils.isNotEmpty(quoteInfoList)) {
            report.getQuoteDetails().addAll(quoteInfoList);
        }
        return report;
    }

    /**
     * 获取报价统计报表
     *
     * @param orderId 采购单ID
     * @return
     * @throws Exception
     */
    public QuoteStatisticReport getQuoteStatisticReport(Long orderId) throws Exception {
        final QuoteStatisticReport report = new QuoteStatisticReport();
        OrderHeader orderHeader = orderService.getOrderHeader(orderId);
        if (orderHeader != null) {
            report.setTitle(orderHeader.getTitle());
            QuoteHeader condition = new QuoteHeader();
            condition.setOrderHeaderId(orderId);
            List<QuoteParam> list = quoteHeaderMapper.queryByCondition(condition);
            if (CollectionUtils.isNotEmpty(list)) {
                report.getStatisticList().addAll(list);
            }
        }
        return report.calculateColumnName();
    }
}