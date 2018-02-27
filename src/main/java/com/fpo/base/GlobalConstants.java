package com.fpo.base;


import org.apache.commons.lang3.StringUtils;

public class GlobalConstants {

    /**
     * 状态 0=删除 1=正常
     */
    public static class State {
        public static final Integer DELETED = 0;
        public static final Integer NORMAL = 1;
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
    public static class VerifyCodeType {
        public static final Integer REGISTER = 1;
        public static final Integer RESET_PWD = 2;
        public static final Integer LOGIN = 3;

        public static String getCacheKey(Integer verifyCodeType) {
            if (verifyCodeType == null) return null;
            if (REGISTER.equals(verifyCodeType)) return CacheKey.SMS_REG_VERIFY_CODE_KEY;
            else if (RESET_PWD.equals(verifyCodeType)) return CacheKey.RESET_PWD_VERIFY_CODE_KEY;
            else if (LOGIN.equals(verifyCodeType)) return CacheKey.SMS_LOGIN_VERIFY_CODE_KEY;
            else return null;
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

        public static final String LIST = "list";
        public static final String CITY_KEY = "data:city";
        public static final String AREA_KEY = "data:area";
        public static final String PRODUCT_KEY = "data:product";
        public static final String SCALE_KEY = "data:scale";

        public static final String APPOINT_FEE_KEY = "data:appointFeeConfig";
    }
}