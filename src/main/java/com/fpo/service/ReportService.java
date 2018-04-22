package com.fpo.service;

import com.fpo.constant.GlobalConstants;
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
     * 报表分析
     *
     * @param orderId    采购单ID
     * @param reportType 报表类型
     * @return
     * @throws Exception
     */
    public Report getReportInfo(Long orderId, Integer reportType) throws Exception {
        //报价汇总
        if (GlobalConstants.TemplateTypeEnum.QUOTE_SUMMARY.getType().equals(reportType)) {
            final QuoteSummaryReport reportInfo = new QuoteSummaryReport();
            OrderHeader orderHeader = orderService.getOrderHeader(orderId);
            if (orderHeader != null) {
                reportInfo.setTitle(orderHeader.getTitle());
                reportInfo.setMinPriceGroup(quoteService.getMinPriceGroup(orderId));
                QuoteHeader condition = new QuoteHeader();
                condition.setOrderHeaderId(orderId);
                List<QuoteParam> list = quoteHeaderMapper.queryByCondition(condition);
                if (CollectionUtils.isNotEmpty(list)) {
                    reportInfo.getSummaryList().addAll(list);
                }
            }

            return reportInfo;
        }
        //单项分析
        else if (GlobalConstants.TemplateTypeEnum.SINGLE_ANALYSIS.getType().equals(reportType)) {
            final SingleAnalysisReport reportInfo = new SingleAnalysisReport();
            OrderParam orderInfo = orderService.getOrderInfo(orderId);
            if (orderInfo != null) {
                reportInfo.setTitle(orderInfo.getTitle());
                reportInfo.getOrderDetails().addAll(orderInfo.getDetails());
            }
            List<QuoteParam> quoteInfoList = quoteService.getQuoteInfoList(orderId);
            if (CollectionUtils.isNotEmpty(quoteInfoList)) {
                reportInfo.getQuoteDetails().addAll(quoteInfoList);
            }

            return reportInfo;
        }
        //报价统计
        else if (GlobalConstants.TemplateTypeEnum.QUOTE_STATISTIC.getType().equals(reportType)) {
            final QuoteStatisticReport reportInfo = new QuoteStatisticReport();
            OrderHeader orderHeader = orderService.getOrderHeader(orderId);
            if (orderHeader != null) {
                reportInfo.setTitle(orderHeader.getTitle());
                QuoteHeader condition = new QuoteHeader();
                condition.setOrderHeaderId(orderId);
                List<QuoteParam> list = quoteHeaderMapper.queryByCondition(condition);
                if (CollectionUtils.isNotEmpty(list)) {
                    reportInfo.getStatisticList().addAll(list);
                }
            }

            return reportInfo.calculateColumnName();
        }

        return null;
    }

}