package com.fpo.service;

import com.alibaba.fastjson.JSONObject;
import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.mapper.TemplateMapper;
import com.fpo.model.OrderDetails;
import com.fpo.model.OrderParam;
import com.fpo.model.Template;
import com.fpo.utils.RedisUtils;
import com.fpo.utils.Reflections;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class TemplateService implements InitializingBean {

    @Resource
    private TemplateMapper templateMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private OrderService orderService;

    public List<Template> getAll() {
        String str = redisUtils.getStr(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY);
        if (StringUtils.isBlank(str)) {
            List<Template> list = templateMapper.selectAll();
            redisUtils.setList(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY, list);
            return list;
        } else {
            return JSONObject.parseArray(str, Template.class);
        }
    }

    /**
     * 获取采购单Excel
     *
     * @return
     * @throws Exception
     */
    public HSSFWorkbook getExcelForPurchaseOrder() throws Exception {
        //获取表头列
        final List<Template> columns = this.getAll();
        //初始化Excel工作簿
        final HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个sheet
        final HSSFSheet sheet = workbook.createSheet("采购清单");
        //新建font实体
        final HSSFFont topFont = workbook.createFont();
        topFont.setFontHeightInPoints((short) 14);
        topFont.setFontName("宋体");
        topFont.setBoldweight(Font.BOLDWEIGHT_BOLD);//粗体
        //顶行样式
        final HSSFCellStyle topCellStyle = workbook.createCellStyle();
        topCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        topCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        topCellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        topCellStyle.setFont(topFont);
        //创建第1行
        final HSSFRow titleRow = sheet.createRow(0);
        //生成表头
        for (int i = 0; i < columns.size(); i++) {
            HSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(columns.get(i).getTitle());
            cell.setCellStyle(topCellStyle);
            sheet.setColumnWidth(i, columns.get(i).getTitle().getBytes().length * 2 * 256);
        }

        return workbook;
    }

    /**
     * 获取报价单Excel
     *
     * @param headerId
     * @return
     * @throws Exception
     */
    public HSSFWorkbook getExcelForQuotation(Long headerId) throws Exception {
        //获取采购单信息
        final OrderParam orderInfo = orderService.getOrderInfo(headerId);
        //获取表头列
        final List<Template> columns = this.getAll();
        //初始化Excel工作簿
        final HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个sheet
        final HSSFSheet sheet = workbook.createSheet("报价单");
        //新建font实体
        final HSSFFont topFont = workbook.createFont();
        topFont.setFontHeightInPoints((short) 14);
        topFont.setFontName("宋体");
        topFont.setBoldweight(Font.BOLDWEIGHT_BOLD);//粗体
        //顶行样式
        final HSSFCellStyle topCellStyle = workbook.createCellStyle();
        topCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        topCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        topCellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        topCellStyle.setFont(topFont);
        //创建第1行
        final HSSFRow topRow = sheet.createRow(0);
        final HSSFCell topCell = topRow.createCell(0);
        topCell.setCellValue(orderInfo.getTitle());
        topCell.setCellStyle(topCellStyle);
        //合并单元格
        CellRangeAddress cra = new CellRangeAddress(0, 0, 0, columns.size() - 1); // 起始行, 终止行, 起始列, 终止列
        sheet.addMergedRegion(cra);
        //创建第2行
        final HSSFRow titleRow = sheet.createRow(1);
        //生成表头
        for (int i = 0; i < columns.size(); i++) {
            HSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(columns.get(i).getTitle());
            cell.setCellStyle(topCellStyle);
            sheet.setColumnWidth(i, columns.get(i).getTitle().getBytes().length * 2 * 256);
        }
        //生成表体
        if (CollectionUtils.isNotEmpty(orderInfo.getDetails())) {
            for (int i = 0; i < orderInfo.getDetails().size(); i++) {
                HSSFRow detailRow = sheet.createRow(i + 2);
                for (int j = 0; j < columns.size(); j++) {
                    HSSFCell cell = detailRow.createCell(j);
                    Object cellValue = Reflections.invokeGetter(orderInfo.getDetails().get(i), columns.get(j).getFiled());
                    if (cellValue != null) {
                        if (columns.get(j).getJavaType().equals(1)) {
                            cell.setCellValue(cellValue.toString());
                        } else if (columns.get(j).getJavaType().equals(2)) {
                            cell.setCellValue((Integer) cellValue);
                        } else if (columns.get(j).getJavaType().equals(3)) {
                            cell.setCellValue(DateUtil.getExcelDate((Date) cellValue));
                        }
                    }
                }
            }
        }

        return workbook;
    }

    public List<OrderDetails> excelImport(MultipartFile file) throws Exception {
        File uploadDir = new File("C:\\temp\\fileupload");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        //新建一个文件
        File tempFile = new File("C:\\temp\\fileupload\\" + new Date().getTime() + ".xls");
        InputStream is = null;
        List<OrderDetails> result = null;
        try {
            file.transferTo(tempFile);
            is = new FileInputStream(tempFile);
            Workbook workbook = new HSSFWorkbook(is);
            result = checkAndFetchExcelData(workbook);
        } catch (Exception e) {
            throw e;
        } finally {
            tempFile.deleteOnExit();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                }
            }
        }
        return result;
    }

    /**
     * 校验excel数据
     *
     * @param workbook
     * @return
     * @throws Exception
     */
    private List<OrderDetails> checkAndFetchExcelData(Workbook workbook) throws Exception {
        if (workbook == null) throw new BaseException("上传文件不能为空");
        Sheet sheet0 = workbook.getSheetAt(0);
        Row headRow = sheet0.getRow(0);
        final List<Template> template = templateMapper.selectAll();
        final Map<Integer, Template> maps = Maps.newHashMap();
        for (int i = 0; i < headRow.getPhysicalNumberOfCells(); i++) {
            String title = headRow.getCell(i).getStringCellValue();
            boolean headValid = false;
            for (Template t : template) {
                if (title.equals(t.getTitle())) {
                    headValid = true;
                    maps.put(i, t);
                }
            }
            if (!headValid) throw new BaseException("模板不合法，请重新下载模板");
        }

        final List<OrderDetails> result = Lists.newArrayList();
        for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
            Row bodyRow = sheet0.getRow(i);
            Class<?> clazz = OrderDetails.class;
            Object o = clazz.newInstance();
            for (int j = 0; j < bodyRow.getPhysicalNumberOfCells(); j++) {
                Method method = null;
                Template templateInfo = maps.get(j);
                if (templateInfo != null) {
                    if (templateInfo.getJavaType().equals(1)) {
                        method = clazz.getDeclaredMethod("set" + templateInfo.getFiled().substring(0, 1).toUpperCase() + templateInfo.getFiled().substring(1), String.class);
                        method.invoke(o, bodyRow.getCell(j).getStringCellValue());
                    } else if (templateInfo.getJavaType().equals(2)) {
                        method = clazz.getDeclaredMethod("set" + templateInfo.getFiled().substring(0, 1).toUpperCase() + templateInfo.getFiled().substring(1), Integer.class);
                        method.invoke(o, (int) bodyRow.getCell(j).getNumericCellValue());
                    } else if (templateInfo.getJavaType().equals(3)) {
                        method = clazz.getDeclaredMethod("set" + templateInfo.getFiled().substring(0, 1).toUpperCase() + templateInfo.getFiled().substring(1), Date.class);
                        method.invoke(o, bodyRow.getCell(j).getDateCellValue());
                    }
                }
            }
            result.add((OrderDetails) o);
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisUtils.delete(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY);
        List<Template> list = templateMapper.selectAll();
        redisUtils.setList(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY, list);
        this.getAll();
    }
}
