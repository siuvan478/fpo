package com.fpo.utils;

import com.fpo.model.UserEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginUtil {

    private static final ThreadLocal<UserEntity> userThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();

    /**
     * 获取当前用户id
     */
    public static Long getUserId() {
        UserEntity userEntity = getUser();
        if (userEntity != null) {
            return userEntity.getId();
        }
        return null;
    }

    /**
     * 获取当前用户名
     */
    public static String getLoginName() {
        UserEntity userEntity = getUser();
        if (userEntity != null) {
            return userEntity.getUsername();
        }
        return null;
    }

    /**
     * 获取当前token
     */
    public static String getToken() {
        UserEntity userEntity = getUser();
        if (userEntity != null) {
            return userEntity.getToken();
        }
        return null;
    }

    public static UserEntity getUser() {
        return userThreadLocal.get();
    }

    public static void setUserJson(UserEntity userEntity) {
        userThreadLocal.set(userEntity);
    }

    public static void setHttpRequest(HttpServletRequest httpRequest) {
        requestThreadLocal.set(httpRequest);
    }

    public static void setHttpResponse(HttpServletResponse httpResponse) {
        responseThreadLocal.set(httpResponse);
    }
}