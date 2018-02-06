package com.fpo.filter;


import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;
import com.alibaba.fastjson.JSON;
import com.fpo.base.CacheKey;
import com.fpo.base.ResultData;
import com.fpo.model.UserEntity;
import com.fpo.utils.LoginUtil;
import com.fpo.utils.RedisUtils;
import com.fpo.utils.WafRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录过滤器
 */
@Component
@ServletComponentScan
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(LoginCheckFilter.class);

    private final static Integer UNLOGIN_CODE = 401;

    private final static String UNLOGIN_MESSAGE = "请先登录";

    private PatternMatcher pathMatcher = new ServletPathMatcher();

    @Value("${login.excludePaths}")
    private String initExcludePaths;

    private Set<String> excludePathSet;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (StringUtils.isNotBlank(initExcludePaths)) {
            excludePathSet = new HashSet<>(10);
            Collections.addAll(this.excludePathSet, initExcludePaths.trim().split(","));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final WafRequestWrapper httpRequestWrapper = new WafRequestWrapper(httpRequest);
        LoginUtil.setHttpRequest(httpRequestWrapper);

        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        LoginUtil.setHttpResponse(httpResponse);

        final String token = httpRequest.getHeader("x-token");
        if (filterPath(httpRequest.getRequestURI())) {
            if (StringUtils.isBlank(token)) {
                this.redirect(httpResponse, UNLOGIN_MESSAGE, UNLOGIN_CODE);
                return;
            }
            //token失效
            UserEntity userEntity = redisUtils.get(CacheKey.TOKEN_KEY + token, UserEntity.class);
            if (userEntity == null) {
                this.redirect(httpResponse, UNLOGIN_MESSAGE, UNLOGIN_CODE);
                return;
            }
            LoginUtil.setUserJson(userEntity);
        }
        filterChain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {

    }

    private void redirect(HttpServletResponse response, String appMessage, Integer appCode)
            throws IOException {
        ResultData<String> resultData = new ResultData<>(null, appMessage, appCode);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(JSON.toJSONString(resultData));
        out.flush();
        out.close();
    }

    private boolean filterPath(String requestURI) {
        if (this.excludePathSet.contains("/*")) {
            return true;
        }
        for (String excludePath : this.excludePathSet) {
            if (pathMatcher.matches(excludePath, requestURI)) {
                return false;
            }
        }
        return true;
    }
}