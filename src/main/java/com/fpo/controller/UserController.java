package com.fpo.controller;

import com.fpo.base.BaseException;
import com.fpo.base.ResultData;
import com.fpo.model.UserParam;
import com.fpo.model.UserProfile;
import com.fpo.service.UserService;
import com.fpo.utils.Identities;
import com.fpo.utils.VerifyCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    /**
     * 用户注册
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> register(@RequestBody UserParam userParam)
            throws Exception {
        userService.registerUser(userParam);
        return new ResultData<>(true);
    }

    /**
     * 用户登录
     *
     * @param userParam
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> login(@RequestBody UserParam userParam, HttpServletResponse response)
            throws Exception {
        String token = Identities.uuid2();
        response.setHeader("x-token", token);
        userService.login(userParam, token);
        return new ResultData<>(true);
    }

    /**
     * 发送各种验证码
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> sendVerifyCode(@RequestBody UserParam userParam)
            throws Exception {
        userService.sendVerifyCode(userParam);
        return new ResultData<>(true);
    }

    /**
     * 忘记密码
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> forgetPassword(@RequestBody UserParam userParam)
            throws Exception {
        userService.resetPassword(userParam);
        return new ResultData<>(true);
    }

    /**
     * 获取个人信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResultData<UserProfile> updateProfile()
            throws Exception {
        return new ResultData<>(userService.getUserInfo());
    }

    /**
     * 更新个人信息
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/profile/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> updateProfile(@RequestBody UserParam userParam)
            throws Exception {
        userService.updateProfile(userParam);
        return new ResultData<>(true);
    }

    /**
     * 获取验证码图片
     *
     * @param response
     */
    @RequestMapping(value = "/front/getVerifyCode", method = RequestMethod.GET)
    public void getPictureVerifyCode(HttpServletResponse response) throws Exception {
        String imageKey = Identities.uuid2();
        response.setContentType("image/jpeg");
        response.setHeader("Content-Disposition", "attachment;filename=verifyCode.jpg");
        response.setHeader("imageKey", imageKey);
        VerifyCodeUtils.outputImage(200, 80, response.getOutputStream(), userService.getPictureVerifyCode(imageKey));
    }

    /**
     * 单独校验图片验证码
     *
     * @param userParam
     * @return
     */
    @RequestMapping(value = "/front/validVerifyCode", method = RequestMethod.GET)
    public ResultData<Boolean> validPictureVerifyCode(@RequestBody UserParam userParam) throws Exception {
        userService.validPictureVerifyCode(userParam);
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