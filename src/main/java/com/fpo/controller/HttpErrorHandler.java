package com.fpo.controller;

import com.fpo.base.HttpStateEnum;
import com.fpo.base.ResultData;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/4/13 0013.
 */
@RestController
public class HttpErrorHandler implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResultData<Void> error() {
        return new ResultData<>(null, HttpStateEnum.NOT_FOUND.getDesc(), HttpStateEnum.NOT_FOUND.code);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
