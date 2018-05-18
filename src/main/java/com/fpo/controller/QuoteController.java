package com.fpo.controller;

import com.fpo.annotation.SkipLoginCheck;
import com.fpo.base.ResultData;
import com.fpo.model.QuoteHeader;
import com.fpo.model.QuoteParam;
import com.fpo.service.QuoteService;
import com.fpo.utils.LoginUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/quote")
public class QuoteController {

    @Resource
    private QuoteService quoteService;

    @SkipLoginCheck(value = "/quote/addOrUpdate")
    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultData<String> addOrUpdate(@RequestBody QuoteParam quoteParam) throws Exception {
        return new ResultData<>(this.quoteService.addOrUpdate(quoteParam));
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public ResultData<PageInfo<QuoteParam>> page(@RequestParam(required = false) Integer pageNum,
                                                 @RequestParam(required = false) Integer pageSize) throws Exception {
        QuoteHeader condition = new QuoteHeader();
        condition.setUserId(LoginUtil.getUserId());
        return new ResultData<>(this.quoteService.pageQueryQuote(pageNum, pageSize, condition));
    }
}