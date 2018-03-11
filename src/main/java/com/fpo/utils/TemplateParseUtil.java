package com.fpo.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/11 0011.
 */
public class TemplateParseUtil {

    /**
     * 解析模板生成Excel
     *
     * @param templateDir  模板目录
     * @param templateName 模板名称
     * @param excelPath    生成的Excel文件路径
     * @param data         数据参数
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     */
    public static void parse(String templateDir, String templateName, String excelPath, Map<String, Object> data)
            throws IOException, TemplateException {
        //初始化工作
        Configuration cfg = new Configuration();
        //设置默认编码格式为UTF-8
        cfg.setDefaultEncoding("UTF-8");
        //全局数字格式
        cfg.setNumberFormat("0.00");
        //设置模板文件位置
        cfg.setDirectoryForTemplateLoading(new File(templateDir));
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        //加载模板
        Template template = cfg.getTemplate(templateName, "utf-8");
        OutputStreamWriter writer = null;
        try {
            //填充数据至Excel
            writer = new OutputStreamWriter(new FileOutputStream(excelPath), "UTF-8");
            template.process(data, writer);
            writer.flush();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }


    /**
     * 解析模板返回字节数组
     *
     * @param templateDir  模板目录
     * @param templateName 模板名称
     * @param data         数据参数
     * @throws IOException
     * @throws TemplateException
     */
    public static byte[] parse(String templateDir, String templateName, Map<String, Object> data) throws TemplateException, IOException {
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");
        cfg.setNumberFormat("0.00");
        cfg.setDirectoryForTemplateLoading(new File(templateDir));
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        Template template = cfg.getTemplate(templateName, "utf-8");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(outStream, "UTF-8");
        template.process(data, out);
        return outStream.toByteArray();
    }

    /**
     * 自定义模板字符串解析
     *
     * @param templateStr 模板字符串
     * @param data        数据
     * @return 解析后的字符串
     * @throws IOException
     * @throws TemplateException
     */
    public static String parse(String templateStr, Map<String, Object> data)
            throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setNumberFormat("#.##");
        //设置装载模板
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("myTemplate", templateStr);
        cfg.setTemplateLoader(stringLoader);
        //加载装载的模板
        Template temp = cfg.getTemplate("myTemplate", "utf-8");
        Writer out = new StringWriter();
        temp.process(data, out);
        return out.toString();
    }
}
