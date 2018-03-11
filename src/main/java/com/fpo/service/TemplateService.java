package com.fpo.service;

import com.alibaba.fastjson.JSONObject;
import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.mapper.TemplateMapper;
import com.fpo.model.OrderDetailsParam;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class TemplateService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);

    @Resource
    private TemplateMapper templateMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private OrderService orderService;

    public List<Template> selectListByType(Integer type) {
        String str = redisUtils.getStr(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY);
        List<Template> list = new ArrayList<>();
        List<Template> result = new ArrayList<>();
        if (StringUtils.isBlank(str)) {
            list.addAll(templateMapper.selectAll());
            redisUtils.setList(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY, list);
        } else {
            list.addAll(JSONObject.parseArray(str, Template.class));
        }
        for (Template t : list) {
            if (type.equals(GlobalConstants.TemplateTypeEnum.ORDER.getType())) {
                if (t.getType().equals(GlobalConstants.TemplateTypeEnum.ORDER.getType())) {
                    result.add(t);
                }
            } else if (type.equals(GlobalConstants.TemplateTypeEnum.QUOTE.getType())) {
                result.add(t);
            }
        }

        return result;
    }

    public Map<String, Object> getFreeMarkerDataModel(Integer type, Long orderId) throws Exception {
        Map<String, Object> dataModel = Maps.newHashMap();
        List<Template> columns = this.selectListByType(type);
        if (CollectionUtils.isNotEmpty(columns)) {
            for (int i = 1; i <= columns.size(); i++) {
                dataModel.put("column" + i, columns.get(i - 1).getTitle());
            }
            //报价单模板
            if (type.equals(GlobalConstants.TemplateTypeEnum.QUOTE.getType())) {
                //获取采购单信息
                final OrderParam orderInfo = orderService.getOrderInfo(orderId);
                dataModel.put("title", orderInfo.getTitle());
                dataModel.put("orderDetails", orderInfo.getDetails());
            }
        }

        return dataModel;
    }

    /**
     * 获取采购单Excel
     *
     * @return
     * @throws Exception
     */
    public HSSFWorkbook getExcelForPurchaseOrder() throws Exception {
        //获取表头列
        final List<Template> columns = this.selectListByType(GlobalConstants.TemplateTypeEnum.ORDER.getType());
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
        final List<Template> columns = this.selectListByType(GlobalConstants.TemplateTypeEnum.QUOTE.getType());
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
                            if (cellValue instanceof Integer)
                                cell.setCellValue((Integer) cellValue);
                            else if (cellValue instanceof Long)
                                cell.setCellValue((Long) cellValue);
                        } else if (columns.get(j).getJavaType().equals(3)) {
                            cell.setCellValue(DateUtil.getExcelDate((Date) cellValue));
                        }
                    }
                }
            }
        }

        return workbook;
    }

    public static final String TEMP_UPLOAD_FOLDER = "c:/usr/tmp/upload/";

    /**
     * 导入报价单
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<OrderDetailsParam> uploadPurchaseOrder(MultipartFile file) throws Exception {
        //新建一个临时目录
        File uploadDir = new File(TEMP_UPLOAD_FOLDER);
        if (!uploadDir.exists()) {
            boolean mkdirs = uploadDir.mkdirs();
        }
        //新建一个临时文件
        File tempFile = new File(TEMP_UPLOAD_FOLDER + new Date().getTime() + ".xls");
        InputStream is = null;
        List<OrderDetailsParam> result = null;
        try {
            file.transferTo(tempFile);
            is = new FileInputStream(tempFile);
            Workbook workbook = new HSSFWorkbook(is);
            result = checkAndFetchExcelData(workbook, 1);
        } catch (BaseException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new BaseException("数据解析失败");
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
     * @param workbook     文件
     * @param templateType 模板类型
     * @return
     * @throws Exception
     * @see com.fpo.base.GlobalConstants.TemplateTypeEnum
     */
    private List<OrderDetailsParam> checkAndFetchExcelData(Workbook workbook, Integer templateType) throws Exception {
        if (workbook == null) throw new BaseException("上传文件不能为空");
        Sheet sheet0 = workbook.getSheetAt(0);
        Row headRow = sheet0.getRow(0);
        final List<Template> template = this.selectListByType(templateType);
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

        final List<OrderDetailsParam> result = Lists.newArrayList();
        for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
            Row bodyRow = sheet0.getRow(i);
            OrderDetailsParam obj = new OrderDetailsParam();
            for (int j = 0; j < bodyRow.getPhysicalNumberOfCells(); j++) {
                Template templateInfo = maps.get(j);
                if (templateInfo != null) {
                    Object cellValue = getCellValue(bodyRow.getCell(j));
                    if (cellValue == null && GlobalConstants.YesOrNo.YES.equals(templateInfo.getRequired())) {
                        throw new BaseException(MessageFormat.format("第{0}行第{1}列为必填项", (i + 1), (j + 1)));
                    } else {
                        try {
                            Reflections.invokeSetter(obj, templateInfo.getFiled(), cellValue);
                        } catch (Exception e) {
                            throw new BaseException(MessageFormat.format("第{0}行第{1}列填入有误", (i + 1), (j + 1)));
                        }
                    }
                }
            }
            result.add(obj);
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisUtils.delete(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY);
        List<Template> list = templateMapper.selectAll();
        redisUtils.setList(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY, list);
    }

    private Object getCellValue(Cell cell) throws BaseException {
        Object value = null;
        // 以下是判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue() + "";
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (date != null) {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                    } else {
                        value = "";
                    }
                } else {
                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING: // 字符串
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN: // Boolean
                value = cell.getBooleanCellValue() + "";
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                value = cell.getCellFormula() + "";
                break;
            case Cell.CELL_TYPE_BLANK: // 空值
                break;
            case Cell.CELL_TYPE_ERROR: // 故障
                throw new BaseException("非法字符");
            default:
                //未知类型
                break;
        }
        return value;
    }
}
