package com.fpo.controller;

import com.fpo.base.BaseException;
import com.fpo.base.ResultData;
import com.fpo.model.UserParam;
import com.fpo.service.UserService;
import com.fpo.utils.Identities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResultData<Boolean> register(@RequestBody UserParam userParam)
            throws Exception {
        userService.registerUser(userParam);
        return new ResultData<>(true);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResultData<Boolean> login(@RequestBody UserParam userParam, HttpServletResponse response)
            throws Exception {
        String token = Identities.uuid2();
        userService.login(userParam, token);
        response.setHeader("x-token", token);
        return new ResultData<>(true);
    }

    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResultData<Boolean> sendVerifyCode(@RequestBody UserParam userParam)
            throws Exception {
        userService.sendVerifyCode(userParam);
        return new ResultData<>(true);
    }

    @RequestMapping("/ex1")
    public Object throwBaseException() throws Exception {
        throw new BaseException("This is BaseException.");
    }

    @RequestMapping("/ex2")
    public Object throwIOException() throws Exception {
        throw new IOException("This is IOException.");
    }


}