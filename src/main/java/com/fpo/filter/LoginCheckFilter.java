package com.fpo.filter;


import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;
import com.alibaba.fastjson.JSON;
import com.fpo.annotation.SkipLoginCheck;
import com.fpo.base.HttpStateEnum;
import com.fpo.base.ResultData;
import com.fpo.constant.GlobalConstants;
import com.fpo.model.UserEntity;
import com.fpo.utils.LoginUtil;
import com.fpo.utils.RedisUtils;
import com.fpo.utils.WafRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录过滤器
 */
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(LoginCheckFilter.class);

    private static final String CONTROLLER_BASE_PACKAGE = "com.fpo.controller";

    private PatternMatcher pathMatcher = new ServletPathMatcher();

    @Value("${login.excludePaths}")
    private String initExcludePaths;

    private Set<String> excludePathSet;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        logger.info("LoginCheckFilter init...");

        if (StringUtils.isNotBlank(initExcludePaths)) {
            excludePathSet = new HashSet<>(10);
            Collections.addAll(this.excludePathSet, initExcludePaths.trim().split(","));

            logger.info("Web config login filter urls-->" + excludePathSet.toString());
        }

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        Set<BeanDefinition> controllers = provider.findCandidateComponents(CONTROLLER_BASE_PACKAGE);
        for (BeanDefinition s : controllers) {

            try {
                Class<?> controller = Class.forName(s.getBeanClassName());

                Method[] methods = controller.getMethods();

                for (Method method : methods) {

                    if (method.isAnnotationPresent(SkipLoginCheck.class) && method.isAnnotationPresent(RequestMapping.class)) {
                        SkipLoginCheck methodAnnotation = method.getAnnotation(SkipLoginCheck.class);
                        excludePathSet.add(methodAnnotation.value());
                        logger.info("Annotation config login filter url-->" + methodAnnotation.value());
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("登录过滤器加载失败", e);
            }
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
                this.redirect(httpResponse);
                return;
            }
            //token失效
            UserEntity userEntity = redisUtils.get(GlobalConstants.CacheKey.TOKEN_KEY + token, UserEntity.class);
            if (userEntity == null) {
                this.redirect(httpResponse);
                return;
            }
            LoginUtil.setUserJson(userEntity);
        }
        filterChain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {

    }

    private void redirect(HttpServletResponse response)
            throws IOException {
        ResultData<String> resultData = new ResultData<>(null, HttpStateEnum.NEED_LOGIN.desc, HttpStateEnum.NEED_LOGIN.code);
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