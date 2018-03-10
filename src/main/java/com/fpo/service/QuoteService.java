package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.mapper.QuoteDetailsMapper;
import com.fpo.mapper.QuoteHeaderMapper;
import com.fpo.model.QuoteDetails;
import com.fpo.model.QuoteDetailsParam;
import com.fpo.model.QuoteHeader;
import com.fpo.model.QuoteParam;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.LoginUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class QuoteService {

    @Resource
    private QuoteHeaderMapper quoteHeaderMapper;

    @Resource
    private QuoteDetailsMapper quoteDetailsMapper;

    @Transactional
    public Long addOrUpdate(QuoteParam p) throws Exception {
        this.validateParam(p);
        Long result = null;
        //新增
        if (p.getId() == null) {
            final QuoteHeader header = BeanMapper.map(p, QuoteHeader.class);
            header.setStatus(GlobalConstants.State.NORMAL);
            header.setCreateDate(new Date());
            header.setUpdateDate(new Date());
            header.setUserId(LoginUtil.getUserId());
            header.setUserId(6L);
            this.quoteHeaderMapper.insert(header);
            result = header.getId();
        }
        //修改
        else {
            final QuoteHeader header = this.quoteHeaderMapper.selectByPrimaryKey(p.getId());
            if (header == null) {
                throw new BaseException("报价单不存在");
            }
            BeanMapper.copy(p, header);
            header.setUpdateDate(new Date());
            this.quoteHeaderMapper.updateByPrimaryKey(header);
            this.quoteDetailsMapper.deleteByHeaderId(header.getId());
            result = header.getId();
        }

        if (CollectionUtils.isNotEmpty(p.getDetails())) {
            for (QuoteDetailsParam d : p.getDetails()) {
                final QuoteDetails details = BeanMapper.map(d, QuoteDetails.class);
                details.setStatus(GlobalConstants.State.NORMAL);
                details.setCreateDate(new Date());
                details.setUpdateDate(new Date());
                details.setHeaderId(result);
                this.quoteDetailsMapper.insert(details);
            }
        }

        return result;
    }

    /**
     * 分页查询报价
     *
     * @param pageNum  当前页
     * @param pageSize 每页显示
     * @return
     * @throws Exception
     */
    public PageInfo<QuoteParam> pageQueryQuote(Integer pageNum, Integer pageSize, QuoteHeader condition) throws Exception {
        //开始分页
        PageHelper.startPage(pageNum, pageSize);
        List<QuoteParam> list = quoteHeaderMapper.queryByCondition(condition);
        return new PageInfo<>(list);
    }

    private void validateParam(QuoteParam p) throws Exception {
        if (CollectionUtils.isNotEmpty(p.getDetails())) {
            for (QuoteDetailsParam d : p.getDetails()) {
                if (d.getOrderDetailId() == null) {
                    throw new BaseException("参数有误");
                }
                if (d.getUnitPrice() == null || d.getUnitPrice().doubleValue() <= 0) {
                    throw new BaseException("单价填写有误");
                }
                if (d.getQuantity() == null || d.getQuantity() <= 0) {
                    throw new BaseException("数量填写有误");
                }
            }
        }
    }
}