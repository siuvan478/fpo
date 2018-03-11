package com.fpo.controller;


import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.base.ResultData;
import com.fpo.model.OrderDetailsParam;
import com.fpo.service.TemplateService;
import com.google.common.collect.Maps;
import freemarker.template.Template;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
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
        if (type.equals(GlobalConstants.TemplateTypeEnum.QUOTE.getType()) && orderId == null) {
            throw new MissingServletRequestParameterException("number", "orderId");
        }
        //获取FreeMarker参数
        final Map<String, Object> data = templateService.getFreeMarkerDataModel(type, orderId);
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate(e.getTemplateName());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +
                new String(e.getExportFileName().getBytes(), "iso-8859-1"));
        template.process(data, response.getWriter());
    }

    /**
     * 下载采购单模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/front/download", method = RequestMethod.GET)
    public void downloadPurchaseTemplate(HttpServletResponse response)
            throws Exception {
        final HSSFWorkbook workbook = templateService.getExcelForPurchaseOrder();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +
                new String(GlobalConstants.TemplateTypeEnum.ORDER.getName().getBytes(), "iso-8859-1") + ".xls");
        workbook.write(response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * 下载报价单模板
     *
     * @param headerId 采购单ID
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/front/download/{headerId}", method = RequestMethod.GET)
    public void downloadQuoteTemplate(@PathVariable Long headerId, HttpServletResponse response)
            throws Exception {
        final HSSFWorkbook workbook = templateService.getExcelForQuotation(headerId);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" +
                new String(GlobalConstants.TemplateTypeEnum.QUOTE.getName().getBytes(), "iso-8859-1") + ".xls");
        workbook.write(response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(value = "/front/index2", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/front/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<List<OrderDetailsParam>> uploadData(@RequestParam(value = "filename") MultipartFile file)
            throws Exception {
        return new ResultData<>(templateService.uploadPurchaseOrder(file));
    }
}
