package com.fpo.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginUtil {

    private static final ThreadLocal<String> userThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();

//    /**
//     * 获取用户id
//     *
//     * @return
//     */
//    public static Long getUserId() {
//        UserEntity userEntity = getUser();
//        if (userEntity != null) {
//            return userEntity.getId();
//        }
//        return null;
//    }
//
//    /**
//     * 获取用户名
//     *
//     * @return
//     */
//    public static String getLoginName() {
//        UserEntity userEntity = getUser();
//        if (userEntity != null) {
//            return userEntity.getLoginName();
//        }
//        return null;
//    }
//
//    /**
//     * 获取token
//     *
//     * @return
//     */
//    public static String getToken() {
//        UserEntity userEntity = getUser();
//        if (userEntity != null) {
//            return userEntity.getToken();
//        }
//        return null;
//    }
//
//    public static UserEntity getUser() {
//        String userJson = userThreadLocal.get();
//        if (userJson != null) {
//            return JSONObject.parseObject(userJson, UserEntity.class);
//        }
//        return null;
//    }

    public static void setUserJson(String userJson) {
        userThreadLocal.set(userJson);
    }

    public static void setHttpRequest(HttpServletRequest httpRequest) {
        requestThreadLocal.set(httpRequest);
    }

    public static void setHttpResponse(HttpServletResponse httpResponse) {
        responseThreadLocal.set(httpResponse);
    }
}