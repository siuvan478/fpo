package com.fpo.base;


import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GlobalConstants {

    /**
     * 是/否
     */
    public static class YesOrNo {
        public static final Integer NO = 0;
        public static final Integer YES = 1;
    }

    /**
     * 状态 0=删除 1=正常
     */
    public static class State {
        public static final Integer DELETED = 0;
        public static final Integer NORMAL = 1;
        public static final Integer STOP_QUOTE = 2;//暂停报价
    }

    /**
     * 登录方式 1=短信 2=密码
     */
    public static class LoginMode {
        public static final Integer SMS = 1;
        public static final Integer PWD = 2;

        public static boolean validate(Integer loginMode) {
            return loginMode != null && (loginMode.equals(SMS) || loginMode.equals(PWD));
        }
    }

    /**
     * 短信验证码类型 1=注册 2=重置密码 3=短信登录
     */
    public enum VerifyCodeTypeEnum {

        REGISTER(1, CacheKey.SMS_REG_VERIFY_CODE_KEY, "SMS_126620104"),
        RESET_PWD(2, CacheKey.RESET_PWD_VERIFY_CODE_KEY, "SMS_126635083"),
        LOGIN(3, CacheKey.SMS_LOGIN_VERIFY_CODE_KEY, "SMS_126640083");

        VerifyCodeTypeEnum(Integer type, String cacheKey, String templateCode) {
            this.type = type;
            this.cacheKey = cacheKey;
            this.templateCode = templateCode;
        }

        public Integer type;
        public String cacheKey;
        public String templateCode;

        public static VerifyCodeTypeEnum getInstance(Integer type) {
            List<VerifyCodeTypeEnum> enumList = EnumUtils.getEnumList(VerifyCodeTypeEnum.class);
            for (VerifyCodeTypeEnum e : enumList) {
                if (e.getType().equals(type))
                    return e;
            }
            return null;
        }

        public Integer getType() {
            return type;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public String getTemplateCode() {
            return templateCode;
        }

    }

    /**
     * 发票方式 0=不用发票 1=增值税普票 2=增值税专票
     */
    public static class InvoiceMode {
        public static final Integer DO_NOT = 0;
        public static final Integer GENERAL = 1;
        public static final Integer SPECIAL = 2;

        public static boolean validate(Integer invoiceMode) {
            return invoiceMode != null && (invoiceMode.equals(DO_NOT) || invoiceMode.equals(GENERAL) || invoiceMode.equals(SPECIAL));
        }
    }

    /**
     * 报价要求 1=报价含税 2=报价含运费
     */
    public static class QuoteMode {
        public static final String CONTAIN_TAX = "1";
        public static final String CONTAIN_FREIGHT = "2";

        public static boolean validate(String quoteMode) {
            return StringUtils.isNotBlank(quoteMode);
        }
    }

    /**
     * 付款方式 0=其他 1=收货后付款 2=预付款
     */
    public static class PaymentMode {
        public static final Integer OTHER = 0;
        public static final Integer PAY_AFTER_RECEIVING = 1;
        public static final Integer ADVANCE_PAY = 2;

        public static boolean validate(Integer paymentMode) {
            return paymentMode != null && (paymentMode.equals(OTHER) || paymentMode.equals(PAY_AFTER_RECEIVING) || paymentMode.equals(ADVANCE_PAY));
        }
    }

    /**
     * 模板枚举
     */
    public enum TemplateTypeEnum {
        ORDER(1, "采购清单模板", "采购清单模板.ftl", "采购清单模板.xls", 0),
        QUOTE(2, "报价单模板", "报价单模板.ftl", "报价单模板.xls", 2),
        QUOTE_SUMMARY(3, "报价汇总", "报价汇总.ftl", "报价汇总.xls", 2);


        public Integer type;//模板类型
        public String name;//模板名称
        public String templateName;//模板文件名
        public String exportFileName;//导出excel名
        public Integer titleIndex;//表头下标

        TemplateTypeEnum(Integer type, String name, String templateName, String exportFileName, Integer titleIndex) {
            this.type = type;
            this.name = name;
            this.templateName = templateName;
            this.exportFileName = exportFileName;
            this.titleIndex = titleIndex;
        }

        public static TemplateTypeEnum getInstance(Integer type) {
            List<TemplateTypeEnum> enumList = EnumUtils.getEnumList(TemplateTypeEnum.class);
            for (TemplateTypeEnum e : enumList) {
                if (e.getType().equals(type))
                    return e;
            }
            return null;
        }

        public Integer getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getTemplateName() {
            return templateName;
        }

        public String getExportFileName() {
            return exportFileName;
        }

        public Integer getTitleIndex() {
            return titleIndex;
        }
    }

    /**
     * Redis缓存键
     */
    public static class CacheKey {
        //token用户信息
        public static final String TOKEN_KEY = "security:";
        //userId用户信息
        public static final String USER_ID_KEY = "security:user:";
        //短信注册验证码
        public static final String SMS_REG_VERIFY_CODE_KEY = "security:verifyCode:smsRegister:";
        //重置密码验证码
        public static final String RESET_PWD_VERIFY_CODE_KEY = "security:verifyCode:resetPwd:";
        //短信登录验证码
        public static final String SMS_LOGIN_VERIFY_CODE_KEY = "security:verifyCode:smsLogin:";
        //密码错误次数统计
        public static final String PWD_ERROR_COUNT_KEY = "security:pwdErrorCount:";
        //excel模板列表
        public static final String EXCEL_TEMPLATE_LIST_KEY = "data:template";
        //图片验证码
        public static final String PICTURE_VERIFY_CODE_KEY = "security:verifyCode:picture:";

        public static final String LIST = "list";
        public static final String CITY_KEY = "data:city";
        public static final String AREA_KEY = "data:area";
        public static final String PRODUCT_KEY = "data:product";
        public static final String SCALE_KEY = "data:scale";

        public static final String APPOINT_FEE_KEY = "data:appointFeeConfig";
    }
}