package com.fpo.service;

import com.alibaba.fastjson.JSONObject;
import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.base.HttpStateEnum;
import com.fpo.mapper.UserMapper;
import com.fpo.model.User;
import com.fpo.model.UserEntity;
import com.fpo.model.UserParam;
import com.fpo.model.UserProfile;
import com.fpo.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private static final String HASH_ALGORITHM = "SHA-1";
    private static final int HASH_INTERATIONS = 1024;
    private static final int SALT_SIZE = 8;
    /**
     * 密码最大错误次数
     */
    private static final int MAX_PWD_ERROR_COUNT = 5;
    /**
     * token缓存时间 15天
     */
    private static final long CONVERSATION_KEEP_TIMEOUT = 60 * 60 * 24 * 15;
    /**
     * 验证码缓存时间 5分钟
     */
    private static final long VERIFY_CODE_TIMEOUT = 5 * 60;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private SmsService smsService;

    /**
     * 用户注册
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    public Long registerUser(UserParam userParam) throws Exception {
        //参数校验
        if (StringUtils.isBlank(userParam.getUsername())) throw new BaseException("手机号码不能为空");
        if (StringUtils.isBlank(userParam.getPassword())) throw new BaseException("登录密码不能为空");
        if (StringUtils.isBlank(userParam.getVerifyCode())) throw new BaseException("验证码不能为空");
        //重复注册校验
        if (userMapper.findByUsername(userParam.getUsername()) != null) throw new BaseException("该手机号码已注册，请直接登录");
        //判断验证码
        String smsRegVerifyCode = redisUtils.getStr(GlobalConstants.CacheKey.SMS_REG_VERIFY_CODE_KEY + userParam.getUsername());
        if (StringUtils.isBlank(smsRegVerifyCode) || !smsRegVerifyCode.equals(userParam.getVerifyCode())) {
            throw new BaseException("验证码错误");
        }
        User user = BeanMapper.map(userParam, User.class);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        user.setSalt(Encodes.encodeHex(salt));
        byte[] hashPassword = Digests.sha1(user.getPassword().getBytes(), salt, HASH_INTERATIONS);
        user.setPassword(Encodes.encodeHex(hashPassword));
        user.setRegTime(new Date());
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userParam 参数
     * @param token     token
     * @throws Exception
     */
    public void login(UserParam userParam, String token) throws Exception {
        //参数校验
        if (StringUtils.isBlank(userParam.getUsername())) throw new BaseException("用户名不能为空");
        if (StringUtils.isBlank(userParam.getPassword())) throw new BaseException("密码不能为空");
        if (!GlobalConstants.LoginMode.validate(userParam.getLoginMode())) throw new BaseException("登录方式参数异常");
        //判断用户是否存在
        User userInfo = userMapper.findByUsername(userParam.getUsername());
        //短信登录
        if (GlobalConstants.LoginMode.SMS.equals(userParam.getLoginMode())) {
            String smsLoginVerifyCode = redisUtils.getStr(GlobalConstants.CacheKey.SMS_LOGIN_VERIFY_CODE_KEY + userParam.getUsername());
            if (StringUtils.isBlank(smsLoginVerifyCode) || !smsLoginVerifyCode.equals(userParam.getVerifyCode())) {
                throw new BaseException("验证码错误");
            } else {
                //第一次登录，注册为用户
                if (userInfo == null) {
                    userInfo = new User(userParam.getUsername(), smsLoginVerifyCode);
                    byte[] salt = Digests.generateSalt(SALT_SIZE);
                    userInfo.setSalt(Encodes.encodeHex(salt));
                    byte[] hashPassword = Digests.sha1(userInfo.getPassword().getBytes(), salt, HASH_INTERATIONS);
                    userInfo.setPassword(Encodes.encodeHex(hashPassword));
                    userInfo.setRegTime(new Date());
                    userMapper.insert(userInfo);
                }
            }
        }
        //密码登录
        else if (GlobalConstants.LoginMode.PWD.equals(userParam.getLoginMode())) {
//            //密码错误次数
//            final Integer pwdErrorCount = redisUtils.getInt(GlobalConstants.CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername());
//            if (pwdErrorCount != null && pwdErrorCount >= MAX_PWD_ERROR_COUNT) {
//                throw new BaseException("密码错误次数过多,账户已被锁定");
//            }
            if (userInfo == null) throw new BaseException("用户名或密码错误");
            byte[] hashPassword = Digests.sha1(userParam.getPassword().getBytes(), Encodes.decodeHex(userInfo.getSalt()), HASH_INTERATIONS);
            if (!Encodes.encodeHex(hashPassword).equals(userInfo.getPassword())) {
                //记录用户名密码错误次数
                redisUtils.incr(GlobalConstants.CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername(), 1L);
                final Integer errCount = redisUtils.getInt(GlobalConstants.CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername());
                if (errCount >= MAX_PWD_ERROR_COUNT) {
                    throw new BaseException("密码连续错误超过3次", HttpStateEnum.NEED_CODE.getCode());
                }
                throw new BaseException("用户名或密码错误");
            } else {
                //登录成功清除密码错误次数
                redisUtils.delete(GlobalConstants.CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername());
            }
        }

        //获取历史缓存并清除
        UserEntity historyUserInfo = redisUtils.get(GlobalConstants.CacheKey.USER_ID_KEY + userInfo.getId().toString(), UserEntity.class);
        if (historyUserInfo != null) {
            redisUtils.delete(GlobalConstants.CacheKey.TOKEN_KEY + historyUserInfo.getToken());
        }
        //存入新的用户缓存
        UserEntity newUserInfo = BeanMapper.map(userInfo, UserEntity.class);
        newUserInfo.setToken(token);
        redisUtils.setex(GlobalConstants.CacheKey.USER_ID_KEY + newUserInfo.getId().toString(), newUserInfo, CONVERSATION_KEEP_TIMEOUT);
        redisUtils.setex(GlobalConstants.CacheKey.TOKEN_KEY + token, newUserInfo, CONVERSATION_KEEP_TIMEOUT);
    }

    /**
     * 发送验证码
     *
     * @param userParam
     */
    public void sendVerifyCode(UserParam userParam) throws Exception {
        //参数校验
        if (StringUtils.isBlank(userParam.getUsername())) throw new BaseException("用户名不能为空");
        GlobalConstants.VerifyCodeTypeEnum e = GlobalConstants.VerifyCodeTypeEnum.getInstance(userParam.getVerifyCodeType());
        if (e == null) throw new BaseException("参数错误");
        String key = e.getCacheKey() + userParam.getUsername();
        //验证码
        String verifyCode = Identities.getRandNumber(6);
        //缓存验证码
        redisUtils.setex(key, verifyCode, VERIFY_CODE_TIMEOUT);
        JSONObject o = new JSONObject();
        o.put("code", verifyCode);
        smsService.sendSms(userParam.getUsername(), o, e.getTemplateCode(), null);
    }

    /**
     * 获取用户信息
     *
     * @return
     * @throws Exception
     */
    public UserProfile getUserInfo() throws Exception {
        Long userId = LoginUtil.getUserId();
        if (userId == null) throw new BaseException("用户未登录");
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) throw new BaseException("用户未登录");
        return BeanMapper.map(user, UserProfile.class);
    }

    /**
     * 更新个人信息
     *
     * @param userParam
     * @throws Exception
     */
    public void updateProfile(UserParam userParam) throws Exception {
        Long userId = LoginUtil.getUserId();
        if (userId == null) throw new BaseException("用户未登录");
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) throw new BaseException("用户未登录");
        if (userParam.getNikeName() != null && userParam.getNikeName().length() > 20) {
            throw new BaseException("姓名不能超过20个字符");
        }
        user.setNickName(userParam.getNikeName());
        user.setPosition(userParam.getPosition());
        user.setCompany(userParam.getCompany());
        userMapper.updateByPrimaryKey(user);
        //刷新用户缓存
        UserEntity userCache = redisUtils.get(GlobalConstants.CacheKey.USER_ID_KEY + userId, UserEntity.class);
        userCache.setNickName(userParam.getNikeName());
        userCache.setPosition(userParam.getPosition());
        userCache.setCompany(userParam.getCompany());
        redisUtils.setex(GlobalConstants.CacheKey.USER_ID_KEY + userId, userCache, CONVERSATION_KEEP_TIMEOUT);
        redisUtils.setex(GlobalConstants.CacheKey.TOKEN_KEY + userCache.getToken(), userCache, CONVERSATION_KEEP_TIMEOUT);
    }

    /**
     * 重置密码
     *
     * @param userParam
     * @throws Exception
     */
    public void resetPassword(UserParam userParam) throws Exception {
        if (StringUtils.isBlank(userParam.getUsername())) throw new BaseException("手机号不能为空");
        if (StringUtils.isBlank(userParam.getVerifyCode())) throw new BaseException("验证码不能为空");
        if (StringUtils.isBlank(userParam.getNewPassword())) throw new BaseException("新密码不能为空");
        String resetPwdVerifyCode = redisUtils.getStr(GlobalConstants.CacheKey.RESET_PWD_VERIFY_CODE_KEY + userParam.getUsername());
        if (StringUtils.isBlank(resetPwdVerifyCode) || !resetPwdVerifyCode.equals(userParam.getVerifyCode())) {
            throw new BaseException("验证码错误");
        }
        User userInfo = userMapper.findByUsername(userParam.getUsername());
        if (userInfo == null) throw new BaseException("请先注册");
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        userInfo.setSalt(Encodes.encodeHex(salt));
        byte[] hashPassword = Digests.sha1(userInfo.getPassword().getBytes(), salt, HASH_INTERATIONS);
        userInfo.setPassword(Encodes.encodeHex(hashPassword));
        userMapper.updateByPrimaryKey(userInfo);
    }

    /**
     * 获取图片验证码
     *
     * @return
     * @throws Exception
     */
    public String getPictureVerifyCode() throws Exception {
        String token = LoginUtil.getHeader("x-token");
        if (token == null) {
            throw new BaseException("非法请求");
        }
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String key = GlobalConstants.CacheKey.PICTURE_VERIFY_CODE_KEY + token;
        redisUtils.setex(key, verifyCode, 60L);
        return verifyCode;
    }

    /**
     * 单独验证图片验证码
     *
     * @param userParam
     * @throws Exception
     */
    public void validPictureVerifyCode(UserParam userParam) throws Exception {
        Integer errCount = redisUtils.getInt(GlobalConstants.CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername());
        if (errCount != null && errCount >= MAX_PWD_ERROR_COUNT) {
            if (StringUtils.isBlank(userParam.getVerifyCode())) {
                throw new BaseException("请填写验证码", HttpStateEnum.NEED_CODE.getCode());
            }
            String key = GlobalConstants.CacheKey.PICTURE_VERIFY_CODE_KEY + LoginUtil.getHeader("x-token");
            String code = redisUtils.getStr(key);
            if (StringUtils.isBlank(code)) {
                throw new BaseException("验证码已过期", HttpStateEnum.NEED_CODE.getCode());
            }
            //不区分大小写
            if (!StringUtils.equalsIgnoreCase(code, userParam.getVerifyCode())) {
                throw new BaseException("验证码错误", HttpStateEnum.NEED_CODE.getCode());
            }
        }
    }

}