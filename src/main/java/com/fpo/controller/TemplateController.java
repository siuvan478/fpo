package com.fpo.controller;


import com.fpo.model.Template;
import com.fpo.service.TemplateService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;


/**
 * Created by Siuvan Xia on 2018/2/25 0025.
 */
@Controller("/")
public class TemplateController {

    @Resource
    private TemplateService templateService;

    @RequestMapping(value = "/template/download", method = RequestMethod.GET)
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response)
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

    //导入
    @RequestMapping(value = "/index2", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    //导入
    @RequestMapping(value = "/data/upload", method = RequestMethod.POST)
    public void uploadData(@RequestParam(value = "filename") MultipartFile file)
            throws Exception {
        templateService.excelImport(file);
    }
}
