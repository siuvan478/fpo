package com.fpo.controller;


import com.fpo.base.GlobalConstants;
import com.fpo.base.ResultData;
import com.fpo.model.OrderDetailsParam;
import com.fpo.service.TemplateService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Created by Siuvan Xia on 2018/2/25 0025.
 */
@Controller("/")
public class TemplateController {

    @Resource
    private TemplateService templateService;

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
