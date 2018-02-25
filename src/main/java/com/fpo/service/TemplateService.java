package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.mapper.TemplateMapper;
import com.fpo.model.ExcelData;
import com.fpo.model.Template;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/25 0025.
 */
@Service
public class TemplateService {

    @Resource
    private TemplateMapper templateMapper;

    public List<Template> getAll() {
        return templateMapper.selectAll();
    }

    public void excelImport(MultipartFile file) throws Exception {
        File uploadDir = new File("C:\\temp\\fileupload");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        //新建一个文件
        File tempFile = new File("C:\\temp\\fileupload\\" + new Date().getTime() + ".xls");
        InputStream is = null;
        try {
            file.transferTo(tempFile);
            is = new FileInputStream(tempFile);
            Workbook workbook = new HSSFWorkbook(is);
            checkAndFetchExcelData(workbook);
        } catch (Exception e) {
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                }
            }
        }
    }

    /**
     * 校验excel数据
     *
     * @param workbook
     * @return
     * @throws Exception
     */
    private List<ExcelData> checkAndFetchExcelData(Workbook workbook) throws Exception {
        if (workbook == null) throw new BaseException("上传文件不能为空");
        Sheet sheet0 = workbook.getSheetAt(0);
        Row headRow = sheet0.getRow(0);
        final List<Template> template = templateMapper.selectAll();
        final Map<Integer, String> maps = Maps.newHashMap();
        for (int i = 0; i < headRow.getPhysicalNumberOfCells(); i++) {
            String title = headRow.getCell(i).getStringCellValue();
            boolean headValid = false;
            for (Template t : template) {
                if (title.equals(t.getTitle())) {
                    headValid = true;
                    maps.put(i, t.getColumnName());
                }
            }
            if (!headValid) throw new BaseException("模板不合法，请重新下载模板");
        }

        final List<ExcelData> result = Lists.newArrayList();
        for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
            Row bodyRow = sheet0.getRow(i);
            Class<?> clazz = ExcelData.class;
            Object o = clazz.newInstance();
            for (int j = 0; j < bodyRow.getPhysicalNumberOfCells(); j++) {
                String columnName = maps.get(j);
                Method method = clazz.getDeclaredMethod("set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1), String.class);
                method.invoke(o, bodyRow.getCell(j).getStringCellValue());
            }
            result.add((ExcelData)o);
        }

        return result;
    }
}
