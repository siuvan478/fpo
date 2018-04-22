package com.fpo.controller;

import com.fpo.base.BaseException;
import com.fpo.constant.GlobalConstants;
import com.fpo.base.ResultData;
import com.fpo.model.Report;
import com.fpo.service.ReportService;
import com.google.common.collect.Maps;
import freemarker.template.Template;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/6 0006.
 */
@Controller
@RequestMapping("/front/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void downloadReport(@RequestParam Integer type, @RequestParam Long orderId,
                               HttpServletResponse response)
            throws Exception {
        final GlobalConstants.TemplateTypeEnum e = GlobalConstants.TemplateTypeEnum.getInstance(type);
        if (e == null) {
            throw new BaseException("导出失败");
        }
        //获取FreeMarker参数
        final Map<String, Object> data = Maps.newHashMap();
        //报价单模板
        if (GlobalConstants.TemplateTypeEnum.QUOTE_SUMMARY.getType().equals(type)
                || GlobalConstants.TemplateTypeEnum.SINGLE_ANALYSIS.getType().equals(type)
                || GlobalConstants.TemplateTypeEnum.QUOTE_STATISTIC.getType().equals(type)) {
            data.put("reportInfo", reportService.getReportInfo(orderId, type));
        }

        Template template = freeMarkerConfigurer.getConfiguration().getTemplate(e.getTemplateName());
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +
                new String(e.getExportFileName().getBytes(), "iso-8859-1"));
        template.process(data, response.getWriter());
        response.flushBuffer();
    }

    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResultData<Report> quoteSummary(@RequestParam Long orderId, @RequestParam Integer reportType)
            throws Exception {
        return new ResultData<>(reportService.getReportInfo(orderId, reportType));
    }

}
