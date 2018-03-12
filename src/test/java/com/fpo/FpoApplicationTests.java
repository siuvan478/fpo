package com.fpo;

import com.alibaba.fastjson.JSONObject;
import com.fpo.core.OSSConfig;
import com.fpo.mapper.UserMapper;
import com.fpo.model.*;
import com.fpo.service.*;
import com.fpo.utils.RedisUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Resource
    private QuoteService quoteService;

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

    @Test
    public void testQuoteAddOrUpdate() throws Exception {
        QuoteParam p = new QuoteParam();
        p.setOrderHeaderId(4L);
        p.setRemark("kahjfajklsfhajkldfas");
        p.setCompanyName("first company");
        p.setContact("Siuvan");
        p.setContactInfo("17620021827");

        QuoteDetailsParam d = new QuoteDetailsParam();
        d.setOrderDetailId(3L);
        d.setQuantity(8000);
        d.setUnitPrice(new BigDecimal(0.5));
        p.getDetails().add(d);

        quoteService.addOrUpdate(p);
    }

    @Resource
    private SmsService smsService;

    @Test
    public void testSmsSend() throws Exception {
        smsService.sendSms("17620021827", null, "SMS_0000", null);
    }

    @Test
    public void testDictConfig() throws Exception {
        OrderParam orderInfo = orderService.getOrderInfo(4L);
        System.out.println(JSONObject.toJSONString(orderInfo));
    }


    @Resource
    private OSSConfig ossConfig;

    @Test
    public void testSts() throws Exception {
        ossConfig.sts("purchase/49ebefaf-3454-4709-b398-6353ed82b0bf.txt");
    }

    @Test
    public void testPageQueryQuoteList() throws Exception {
        QuoteHeader condition = new QuoteHeader();
        PageInfo<QuoteParam> quoteParamPageInfo = quoteService.pageQueryQuote(1, 10, condition);
        System.out.printf(JSONObject.toJSONString(quoteParamPageInfo));
    }

    @Resource
    private SmsProducer smsProducer;

    @Test
    public void testSmsQueue(){
        smsProducer.send("nihao");
    }

}
