package com.fpo.controller;

import com.fpo.base.ResultData;
import com.fpo.model.OrderParam;
import com.fpo.service.OrderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}