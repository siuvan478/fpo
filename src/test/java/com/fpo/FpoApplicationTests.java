package com.fpo;

import com.alibaba.fastjson.JSONObject;
import com.fpo.mapper.UserMapper;
import com.fpo.model.OrderDetailsParam;
import com.fpo.model.OrderParam;
import com.fpo.model.User;
import com.fpo.model.UserParam;
import com.fpo.service.OrderService;
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
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FpoApplicationTests {


    @Resource
    UserMapper userMapper;

    @Resource
    UserService userService;

    @Resource
    private OrderService orderService;

    @Test
    public void contextLoads() {
        PageHelper.startPage(2, 1);
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectAll());
        System.out.println(JSONObject.toJSONString(pageInfo.getList()));
    }

    @Test
    public void regUser() throws Exception {
        UserParam user = new UserParam();
        user.setUsername("17620021821");
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

    @Test
    public void testSendVerifyCode() throws Exception {
        UserParam u = new UserParam();
        u.setVerifyCodeType(1);
        u.setUsername("17620021827");
        userService.sendVerifyCode(u);
        u.setVerifyCodeType(2);
        u.setUsername("17620021827");
        userService.sendVerifyCode(u);
        u.setVerifyCodeType(3);
        u.setUsername("17620021827");
        userService.sendVerifyCode(u);
    }

    @Test
    public void testOrderAddOrUpdate() throws Exception {
        OrderParam p = new OrderParam();
        p.setTitle("siuvan的采购单222");
        p.setInvoiceMode(1);
        p.setQuoteMode("1");
        p.setPaymentMode(2);
        p.setReceiptDate(new Date());
        p.setReceiptAddress("新港东路618号");
        p.setContact("Siuvan");
        p.setContactInfo("17620021827");
        p.setUserId(6L);
        p.setId(4L);

        OrderDetailsParam d = new OrderDetailsParam();
        d.setName("钢化膜1");
        d.setQuantity(9999);
        d.setUnit("张");

        p.getDetails().add(d);
        orderService.addOrUpdate(p);
    }

}
