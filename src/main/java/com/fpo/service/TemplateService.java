package com.fpo.service;

import com.alibaba.fastjson.JSONObject;
import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.mapper.TemplateMapper;
import com.fpo.model.OrderDetails;
import com.fpo.model.Template;
import com.fpo.utils.RedisUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

/**
 * Created by Administrator on 2018/2/25 0025.
 */
@Service
public class TemplateService implements InitializingBean {

    @Resource
    private TemplateMapper templateMapper;

    @Autowired
    private RedisUtils redisUtils;

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
