package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.base.CacheKey;
import com.fpo.mapper.UserMapper;
import com.fpo.model.User;
import com.fpo.model.UserEntity;
import com.fpo.model.UserParam;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.Digests;
import com.fpo.utils.Encodes;
import com.fpo.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {

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

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 用户注册
     *
     * @param user
     * @return
     * @throws Exception
     */
    public Long registerUser(User user) throws Exception {
        User userInfo = userMapper.findByUsername(user.getUsername());
        if (userInfo != null) throw new BaseException("用户名已存在");
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
        //密码错误次数
        final Integer pwdErrorCount = redisUtils.getInt(CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername());
        if (pwdErrorCount != null && pwdErrorCount >= MAX_PWD_ERROR_COUNT) {
            throw new BaseException("密码错误次数过多,账户已被锁定");
        }
        //判断用户是否存在
        User userInfo = userMapper.findByUsername(userParam.getUsername());
        if (userInfo == null) throw new BaseException("用户名或密码错误");
        byte[] hashPassword = Digests.sha1(userParam.getPassword().getBytes(), Encodes.decodeHex(userInfo.getSalt()), HASH_INTERATIONS);
        if (!Encodes.encodeHex(hashPassword).equals(userInfo.getPassword())) {
            //记录用户名密码错误次数
            redisUtils.incr(CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername(), 1L);
            throw new BaseException("用户名或密码错误");
        } else {
            //清除密码错误次数
            redisUtils.delete(CacheKey.PWD_ERROR_COUNT_KEY + userParam.getUsername());
        }
        //获取历史缓存并清除
        UserEntity historyUserInfo = redisUtils.get(CacheKey.USER_ID_KEY + userInfo.getId().toString(), UserEntity.class);
        if (historyUserInfo != null) {
            redisUtils.delete(CacheKey.TOKEN_KEY + historyUserInfo.getToken());
        }
        //存入新的用户缓存
        UserEntity newUserInfo = BeanMapper.map(userInfo, UserEntity.class);
        newUserInfo.setToken(token);
        redisUtils.setex(CacheKey.USER_ID_KEY + newUserInfo.getId().toString(), newUserInfo, CONVERSATION_KEEP_TIMEOUT);
        redisUtils.setex(CacheKey.TOKEN_KEY + token, newUserInfo, CONVERSATION_KEEP_TIMEOUT);
    }

}