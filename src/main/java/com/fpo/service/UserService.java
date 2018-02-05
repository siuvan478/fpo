package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.mapper.UserMapper;
import com.fpo.model.User;
import com.fpo.model.UserParam;
import com.fpo.utils.Digests;
import com.fpo.utils.Encodes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    private static final int SALT_SIZE = 8;

    @Resource
    private UserMapper userMapper;

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

    public void login(UserParam userParam, String token) throws Exception {
        if (StringUtils.isBlank(userParam.getUsername())) throw new BaseException("用户名不能为空");
        if (StringUtils.isBlank(userParam.getPassword())) throw new BaseException("密码不能为空");
        User userInfo = userMapper.findByUsername(userParam.getUsername());
        if (userInfo == null) throw new BaseException("用户名或密码错误");
        byte[] hashPassword = Digests.sha1(userParam.getPassword().getBytes(), Encodes.decodeHex(userInfo.getSalt()), HASH_INTERATIONS);
        if (Encodes.encodeHex(hashPassword).equals(userInfo.getPassword())) {
            throw new BaseException("用户名或密码错误");
        }
    }

    public static void main(String[] args) {
        System.out.println("78595828af7b6cb41d1a92a3fa8ec877cd022f70".length());
    }
}