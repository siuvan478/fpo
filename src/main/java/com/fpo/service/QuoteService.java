package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.constant.GlobalConstants;
import com.fpo.mapper.QuoteDetailHistoriesMapper;
import com.fpo.mapper.QuoteDetailsMapper;
import com.fpo.mapper.QuoteHeaderMapper;
import com.fpo.model.*;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.LoginUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuoteService {

    @Resource
    private QuoteHeaderMapper quoteHeaderMapper;

    @Resource
    private QuoteDetailsMapper quoteDetailsMapper;

    @Resource
    private QuoteDetailHistoriesMapper quoteDetailHistoriesMapper;

    @Transactional
    public Long addOrUpdate(QuoteParam p) throws Exception {
        this.validateParam(p);
        Long result = null;
        Date operateTime = new Date();
        //新增
        if (p.getId() == null) {
            final QuoteHeader header = BeanMapper.map(p, QuoteHeader.class);
            header.setStatus(GlobalConstants.State.NORMAL);
            header.setCreateDate(operateTime);
            header.setUpdateDate(operateTime);
            header.setUserId(LoginUtil.getUserId());
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
            header.setUpdateDate(operateTime);
            this.quoteHeaderMapper.updateByPrimaryKey(header);
            this.quoteDetailsMapper.deleteByHeaderId(header.getId());
            result = header.getId();
        }

        if (CollectionUtils.isNotEmpty(p.getDetails())) {
            List<QuoteDetails> detailsList = new ArrayList<>();
            for (QuoteDetailsParam d : p.getDetails()) {
                final QuoteDetails details = new QuoteDetails(result, GlobalConstants.State.NORMAL, operateTime, operateTime);
                BeanMapper.copy(d, details);
                detailsList.add(details);
            }
            quoteDetailsMapper.batchInsert(detailsList);
            quoteDetailHistoriesMapper.batchInsert(BeanMapper.mapList(detailsList, QuoteDetailHistories.class));
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
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<QuoteParam> list = quoteHeaderMapper.queryByCondition(condition);
        return new PageInfo<>(list);
    }

    /**
     * 获取最低报价组合
     *
     * @param orderId 报价单ID
     * @return
     */
    public BigDecimal getMinPriceGroup(Long orderId) {
        return quoteDetailsMapper.getMinPriceGroup(orderId);
    }

    /**
     * 获取报价信息
     *
     * @param orderId
     * @return
     */
    public List<QuoteParam> getQuoteInfoList(Long orderId) {
        return quoteHeaderMapper.getQuoteInfoList(orderId);
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
                if (d.getSupplyQuantity() == null || d.getSupplyQuantity() <= 0) {
                    throw new BaseException("数量填写有误");
                }
            }
        }
    }
}