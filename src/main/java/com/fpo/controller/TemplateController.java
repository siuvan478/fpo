package com.fpo.controller;


import com.fpo.base.ResultData;
import com.fpo.model.OrderDetails;
import com.fpo.model.Template;
import com.fpo.service.TemplateService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping(value = "/front/download", method = RequestMethod.GET)
    public void downloadTemplate(HttpServletResponse response)
            throws Exception {
        List<Template> headers = templateService.getAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(headers.get(i).getTitle());
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=createList.xls");//默认Excel名称
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }

    @RequestMapping(value = "/front/index2", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/front/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<List<OrderDetails>> uploadData(@RequestParam(value = "filename") MultipartFile file)
            throws Exception {
        return new ResultData<>(templateService.excelImport(file));
    }
}
