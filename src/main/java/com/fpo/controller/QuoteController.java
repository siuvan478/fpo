package com.fpo.controller;

import com.fpo.model.QuoteHeader;
import com.fpo.service.QuoteService;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class QuoteController {

    @Resource
    private QuoteService quoteService;

    /**
     * 报表-总价排名
     *
     * @return
     */
    @RequestMapping(value = "/front/report/ranking", method = RequestMethod.GET)
    public Map<String, Object> totalPriceRanking(@RequestParam(required = false) Integer pageSize,
                                                 @RequestParam(required = false) Integer pageNum, @RequestParam Long orderId)
            throws Exception {
        final Map<String, Object> result = Maps.newHashMap();
        QuoteHeader condition = new QuoteHeader();
        condition.setOrderHeaderId(orderId);
        result.put("pageInfo", quoteService.pageQueryQuote(pageNum, pageSize, condition));
        result.put("minPriceGroup", quoteService.getMinPriceGroup(orderId));
        return result;
    }
}