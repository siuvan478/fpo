package com.fpo.controller;


import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.base.ResultData;
import com.fpo.model.OrderDetailsParam;
import com.fpo.model.OrderParam;
import com.fpo.service.OrderService;
import com.fpo.service.ReportService;
import com.fpo.service.TemplateService;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * Created by Siuvan Xia on 2018/2/25 0025.
 */
@Controller("/")
public class TemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);

    @Resource
    private TemplateService templateService;

    @Resource
    private OrderService orderService;

    @Resource
    private ReportService reportService;

    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping(value = "/front/template/download", method = RequestMethod.GET)
    public void downloadTemplate(@RequestParam Integer type, @RequestParam(required = false) Long orderId,
                                 HttpServletResponse response)
            throws Exception {
        final GlobalConstants.TemplateTypeEnum e = GlobalConstants.TemplateTypeEnum.getInstance(type);
        if (e == null) {
            LOGGER.error("模板下载失败, 找不到type = {} TemplateTypeEnum枚举实例", type);
            throw new BaseException("模板下载失败");
        }
        if (!type.equals(GlobalConstants.TemplateTypeEnum.ORDER.getType()) && orderId == null) {
            throw new MissingServletRequestParameterException("number", "orderId");
        }
        //获取FreeMarker参数
        final Map<String, Object> data = templateService.getFreeMarkerDataModel(type);
        //报价单模板
        if (GlobalConstants.TemplateTypeEnum.QUOTE.getType().equals(type)) {
            //获取采购单信息
            final OrderParam orderInfo = orderService.getOrderInfo(orderId);
            data.put("title", orderInfo.getTitle());
            data.put("orderDetails", orderInfo.getDetails());
        }
        //报价汇总
        else if (GlobalConstants.TemplateTypeEnum.QUOTE_SUMMARY.getType().equals(type)) {
            data.put("reportInfo", reportService.getQuoteSummaryReport(orderId));
        }
        //单项分析
        else if (GlobalConstants.TemplateTypeEnum.SINGLE_ANALYSIS.getType().equals(type)) {
            data.put("reportInfo", reportService.getSingleAnalysisReport(orderId));
        }
        //报价统计
        else if (GlobalConstants.TemplateTypeEnum.QUOTE_STATISTIC.getType().equals(type)) {
            data.put("reportInfo", reportService.getQuoteStatisticReport(orderId));
        }
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate(e.getTemplateName());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +
                new String(e.getExportFileName().getBytes(), "iso-8859-1"));
        template.process(data, response.getWriter());
    }

    @RequestMapping(value = "/front/index2", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/front/import", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<List<OrderDetailsParam>> importData(@RequestParam(value = "filename") MultipartFile file, @RequestParam Integer fileType)
            throws Exception {
        return new ResultData<>(templateService.importData(file, fileType));
    }
}
