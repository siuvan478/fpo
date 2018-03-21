package com.fpo.service;

import com.alibaba.fastjson.JSONObject;
import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.mapper.TemplateMapper;
import com.fpo.model.OrderDetailsParam;
import com.fpo.model.Template;
import com.fpo.utils.RedisUtils;
import com.fpo.utils.Reflections;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("templateService")
public class TemplateService implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger("TemplateService");

    @Resource
    private TemplateMapper templateMapper;

    @Autowired
    private RedisUtils redisUtils;

    //临时上传目录
    private static final String TEMP_UPLOAD_FOLDER = "c:/usr/tmp/upload/";

    /**
     * 获取模板缓存数据
     *
     * @param type
     * @return
     */
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
            if (t.getType().equals(type))
                result.add(t);
        }
        return result;
    }

    /**
     * FreeMarker模板需要的参数
     *
     * @param type 模板类型
     * @return
     * @throws Exception
     */
    public Map<String, Object> getFreeMarkerDataModel(Integer type) throws Exception {
        Map<String, Object> dataModel = Maps.newHashMap();
        List<Template> columns = this.selectListByType(type);
        if (CollectionUtils.isNotEmpty(columns)) {
            for (int i = 1; i <= columns.size(); i++) {
                dataModel.put("column" + i, columns.get(i - 1).getTitle());
            }
        }
        return dataModel;
    }


    /**
     * 导入报价单
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<OrderDetailsParam> importData(MultipartFile file, Integer fileType) throws BaseException {
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
            result = parseExcel(tempFile, GlobalConstants.TemplateTypeEnum.getInstance(fileType));
        } catch (BaseException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
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
     * 解析excel
     *
     * @param excelFile
     * @param tEnum
     * @return
     * @throws BaseException
     */
    private List<OrderDetailsParam> parseExcel(File excelFile, GlobalConstants.TemplateTypeEnum tEnum) throws BaseException {
        if (tEnum == null) throw new BaseException("fileType参数缺失或有误");
        final List<OrderDetailsParam> result = Lists.newArrayList();
        final List<Template> template = this.selectListByType(tEnum.getType());
        final Map<Integer, Template> maps = Maps.newHashMap();
        try {
            Document doc = new SAXReader().read(excelFile);
            List allRow = ((Element) doc.getRootElement().elements("Worksheet").get(0)).element("Table").elements("Row");
            //获取表头列
            final Element titleRow = (Element) allRow.get(tEnum.getTitleIndex());
            final List titleCells = titleRow.elements("Cell");
            for (int i = 0; i < titleCells.size(); i++) {
                String title = ((Element) titleCells.get(i)).element("Data").getText();
                boolean headValid = false;
                for (Template t : template) {
                    if (title.equals(t.getTitle())) {
                        headValid = true;
                        maps.put(i, t);//下标放入Map
                        break;
                    }
                }
                if (!headValid) throw new BaseException("模板不合法，请重新下载模板");
            }

            for (int i = tEnum.getTitleIndex() + 1; i < allRow.size(); i++) {
                final Element thisRow = (Element) allRow.get(i);
                OrderDetailsParam obj = new OrderDetailsParam();
                for (int j = 0; j < thisRow.elements("Cell").size(); j++) {
                    final Element thisCell = (Element) thisRow.elements("Cell").get(j);
                    Template templateInfo = maps.get(j);
                    if (templateInfo != null) {
                        String cellValue = thisCell.element("Data").getText();
                        if (cellValue == null && GlobalConstants.YesOrNo.YES.equals(templateInfo.getRequired())) {
                            throw new BaseException(MessageFormat.format("第{0}行第{1}列为必填项", (i + 1), (j + 1)));
                        } else {
                            try {
                                String setterMethodName = Reflections.getSetterMethodName(templateInfo.getFiled());
                                Reflections.invokeMethod(obj, setterMethodName, new Class[]{String.class}, new Object[]{cellValue});
                            } catch (NumberFormatException e) {
                                throw new BaseException(MessageFormat.format("第{0}行第{1}列填入有误", (i + 1), (j + 1)));
                            }
                        }
                    }
                }
                result.add(obj);
            }
        } catch (DocumentException e) {
            throw new BaseException("数据解析失败，请使用下载的模板导入数据");
        } catch (NullPointerException e) {
            //忽略空指针，无效数据行，直接忽略
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisUtils.delete(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY);
        List<Template> list = templateMapper.selectAll();
        redisUtils.setList(GlobalConstants.CacheKey.EXCEL_TEMPLATE_LIST_KEY, list);
    }

}
