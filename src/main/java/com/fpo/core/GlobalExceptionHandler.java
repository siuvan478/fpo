package com.fpo.core;


import com.fpo.base.BaseException;
import com.fpo.base.HttpStateEnum;
import com.fpo.base.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger("GlobalExceptionHandler");

    @ExceptionHandler(value = BaseException.class)
    public Object baseErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("---BaseException Handler---Host {} invokes url {} ERROR: {}", req.getRemoteHost(), req.getRequestURL(), e.getMessage());
        BaseException be = (BaseException) e;
        return new ResultData<>(null, be.getMessage(), be.getErrorCode());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Object missServletRequestParameterErrorHandler(HttpServletRequest req, MissingServletRequestParameterException e) throws Exception {
        logger.error("---MissingServletRequestParameterException Handler---Host {} invokes url {} ERROR: {}", req.getRemoteHost(), req.getRequestURL(), e.getMessage());
        return new ResultData<>(null, e.getMessage(), HttpStateEnum.ERROR.getCode());
    }

    @ExceptionHandler(value = Exception.class)
    public Object defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("---DefaultException Handler---Host {} invokes url {} ERROR: {}", req.getRemoteHost(), req.getRequestURL(), e.getMessage());
        return new ResultData<>(null, "网络异常，请稍后再试", HttpStateEnum.ERROR.getCode());
    }
}