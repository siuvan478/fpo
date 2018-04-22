package com.fpo.controller;

import com.fpo.base.ResultData;
import com.fpo.model.OrderParam;
import com.fpo.service.OrderService;
import com.fpo.utils.LoginUtil;
import com.fpo.vo.OrderMgtVO;
import com.github.pagehelper.PageInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Long> addOrUpdate(@RequestBody OrderParam orderParam)
            throws Exception {
        return new ResultData<>(orderService.addOrUpdate(orderParam));
    }

    @RequestMapping(value = "/preview", method = RequestMethod.GET)
    public ResultData<OrderParam> preview(@RequestParam Long id)
            throws Exception {
        return new ResultData<>(orderService.getOrderInfo(id));
    }

    @RequestMapping(value = "/pause", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> stopQuote(@RequestBody OrderParam orderParam)
            throws Exception {
        orderService.stopQuote(orderParam);
        return new ResultData<>(true);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<Boolean> deleteOrder(@RequestBody OrderParam orderParam)
            throws Exception {
        orderService.deleteOrder(orderParam);
        return new ResultData<>(true);
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public ResultData<PageInfo<OrderMgtVO>> pageQuery(@RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize)
            throws Exception {
        OrderParam orderParam = new OrderParam();
        orderParam.setUserId(LoginUtil.getUserId());
        return new ResultData<>(orderService.pageQueryOrderInfo(pageNum, pageSize, orderParam));
    }
}