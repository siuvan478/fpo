package com.fpo.base;


public class GlobalConstants {

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

        public static final String LIST = "list";
        public static final String CITY_KEY = "data:city";
        public static final String AREA_KEY = "data:area";
        public static final String PRODUCT_KEY = "data:product";
        public static final String SCALE_KEY = "data:scale";

        public static final String APPOINT_FEE_KEY = "data:appointFeeConfig";
    }
}