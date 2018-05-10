package com.fpo;

import com.alibaba.fastjson.JSONObject;
import com.fpo.core.OSSConfig;
import com.fpo.mapper.QuoteDetailsMapper;
import com.fpo.mapper.UserMapper;
import com.fpo.model.*;
import com.fpo.service.*;
import com.fpo.utils.RedisUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Resource
    private AttachmentService attachmentService;

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
    public void testOrderAddOrUpdate() throws Exception {
        OrderParam p = new OrderParam();
        p.setTitle("siuvan的采购单333");
        p.setInvoiceMode(1);
        p.setQuoteMode("1");
        p.setPaymentMode(2);
        p.setReceiptDate(new Date());
        p.setReceiptAddress("新港东路618号");
        p.setContact("Siuvan");
        p.setContactInfo("17620021827");
        p.setUserId(6L);
        // p.setId(4L);

        OrderDetailsParam d = new OrderDetailsParam();
        d.setName("钢化膜1222");
        d.setQuantity(9999);
        d.setUnit("张");

        p.getDetails().add(d);

        ArrayList<Long> attIdLIST = new ArrayList<>();
        attIdLIST.add(1L);
        attIdLIST.add(2L);
        p.setAttIdList(attIdLIST);
        orderService.addOrUpdate(p);
    }

    @Test
    public void testQuoteAddOrUpdate() throws Exception {
        Long orderId = 4L;
        QuoteParam p = new QuoteParam();
        p.setOrderHeaderId(orderId);
        p.setRemark("kahjfajklsfhajkldfas");
        p.setCompanyName("广州钜源泵业有限公司");
        p.setContact("Siuvan");
        p.setContactInfo("17620021827");
        // p.setId(19L);

        List<OrderDetailsParam> orderDetails = orderService.getOrderDetails(orderId);

        for (OrderDetailsParam o : orderDetails) {
            QuoteDetailsParam d1 = new QuoteDetailsParam();
            d1.setOrderDetailId(o.getId());
            d1.setSupplyQuantity(RandomUtils.nextInt(1, 99));
            d1.setUnitPrice(new BigDecimal(RandomUtils.nextInt(100, 999)));
            p.getDetails().add(d1);
        }

        quoteService.addOrUpdate(p);
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

    @Resource
    QuoteDetailsMapper quoteDetailsMapper;

    @Test
    public void testPageQueryQuoteList() throws Exception {
        QuoteHeader condition = new QuoteHeader();
        PageInfo<QuoteParam> quoteParamPageInfo = quoteService.pageQueryQuote(1, 10, condition);
        System.out.printf(JSONObject.toJSONString(quoteParamPageInfo, true));
    }

    @Resource
    private SmsProducer smsProducer;

    @Test
    public void testSmsQueue() {
        smsProducer.send("nihao");
    }

    @Test
    public void testaaa() {
        List<QuoteParam> quoteInfoList = quoteService.getQuoteInfoList(4L);
        System.out.println(JSONObject.toJSONString(quoteInfoList, true));
    }

    @Test
    public void getOrderInfo() throws Exception {
        OrderParam orderInfo = orderService.getOrderInfo(5L);
        System.out.println(JSONObject.toJSONString(orderInfo, true));
    }

    @Autowired
    private ReportService reportService;

    @Test
    public void getReportInfo() throws Exception {
        Report reportInfo = reportService.getReportInfo(4L, 5);
        System.out.println(JSONObject.toJSONString(reportInfo, true));
    }

    @Test
    public void testPageQueryOrderInfo() {
        System.out.println(JSONObject.toJSONString(orderService.pageQueryOrderInfo(1, 10, null), true));
    }

    @Test
    public void testAddAttachment() throws Exception {
        AttachmentParam attachmentParam = new AttachmentParam();
        attachmentParam.setBizId(-1L);
        attachmentParam.setName("test附件1");
        attachmentParam.setSuffix(".txt");
        attachmentParam.setPath("akfhasdjfhlasjkdfhaklsdjf");
        attachmentParam.setBizType(1);
        Long attId = attachmentService.save(attachmentParam);
        System.out.println("保存附件成功：" + attId);
    }

}
