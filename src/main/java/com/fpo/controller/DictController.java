package com.fpo.controller;


import com.fpo.base.ResultData;
import com.fpo.utils.DicUtil;
import com.fpo.vo.DictVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DictController {

    @RequestMapping(value = "/front/dict/list", method = RequestMethod.GET)
    public ResultData<List<DictVO>> list(@RequestParam String dictKey) throws Exception {
        return new ResultData<>(DicUtil.getDictVOs(dictKey));
    }
}
