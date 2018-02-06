package com.fpo;

import com.alibaba.fastjson.JSONObject;
import com.fpo.mapper.UserMapper;
import com.fpo.model.User;
import com.fpo.service.UserService;
import com.fpo.utils.RedisUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FpoApplicationTests {


    @Resource
    UserMapper userMapper;

    @Resource
    UserService userService;

    @Test
    public void contextLoads() {
        PageHelper.startPage(2, 1);
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectAll());
        System.out.println(JSONObject.toJSONString(pageInfo.getList()));
    }

    @Test
    public void regUser() throws Exception{
        User user = new User();
        user.setUsername("17620021827");
        user.setPassword("123456");
        Long userId = userService.registerUser(user);
        System.out.println(userId);
    }

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void testRedisUtil() {
        redisUtils.incr("1:2:3", 1L);
    }

}
