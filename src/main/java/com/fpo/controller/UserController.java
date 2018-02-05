package com.fpo.controller;

import com.fpo.base.BaseException;
import com.fpo.base.ResultData;
import com.fpo.model.User;
import com.fpo.model.UserParam;
import com.fpo.service.UserService;
import com.fpo.utils.Identities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResultData<Boolean> register(@RequestParam String username, @RequestParam String password) throws Exception {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.registerUser(user);
        return new ResultData<>(true);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResultData<Boolean> login(@RequestBody UserParam userParam, HttpServletResponse response) throws Exception {
        String token = Identities.uuid2();
        userService.login(userParam, token);
        response.setHeader("x-token", token);
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