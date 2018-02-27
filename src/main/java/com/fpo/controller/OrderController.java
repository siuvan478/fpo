package com.fpo.controller;

import com.fpo.base.ResultData;
import com.fpo.model.OrderParam;
import com.fpo.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResultData<Long> addOrUpdate(@RequestBody OrderParam orderParam)
            throws Exception {
        return new ResultData<>(orderService.addOrUpdate(orderParam));
    }

    @RequestMapping(value = "/preview", method = RequestMethod.GET)
    public ResultData<OrderParam> preview(@RequestParam Long id)
            throws Exception {
        return new ResultData<>(orderService.getOrderInfo(id));
    }

    @RequestMapping(value = "/pause", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResultData<Boolean> stopQuote(@RequestBody OrderParam orderParam)
            throws Exception {
        orderService.stopQuote(orderParam);
        return new ResultData<>(true);
    }
}