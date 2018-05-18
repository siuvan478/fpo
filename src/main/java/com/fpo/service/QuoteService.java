package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.constant.DictConstants;
import com.fpo.constant.GlobalConstants;
import com.fpo.mapper.QuoteDetailHistoriesMapper;
import com.fpo.mapper.QuoteDetailsMapper;
import com.fpo.mapper.QuoteHeaderMapper;
import com.fpo.model.*;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.Identities;
import com.fpo.utils.LoginUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private UserService userService;

    @Transactional
    public String addOrUpdate(QuoteParam p) throws Exception {
        // 参数校验
        this.validateParam(p);
        // token
        String token = Identities.uuid2();
        // 登录
        UserParam userLoginParam = new UserParam();
        userLoginParam.setUsername(p.getContactInfo());
        userLoginParam.setLoginMode(GlobalConstants.LoginMode.SMS);
        userLoginParam.setVerifyCode(p.getVerifyCode());
        Long userId = userService.login(userLoginParam, token);
        // 报价单ID
        Long quoteId = null;
        // 当前时间
        Date operateTime = new Date();
        //新增
        if (p.getId() == null) {
            final QuoteHeader header = BeanMapper.map(p, QuoteHeader.class);
            header.setStatus(GlobalConstants.State.NORMAL);
            header.setCreateDate(operateTime);
            header.setUpdateDate(operateTime);
            header.setUserId(userId);
            this.quoteHeaderMapper.insert(header);
            quoteId = header.getId();
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
            quoteId = header.getId();
        }

        // 采购单明细
        if (CollectionUtils.isNotEmpty(p.getDetails())) {
            List<QuoteDetails> detailsList = new ArrayList<>();
            for (QuoteDetailsParam d : p.getDetails()) {
                final QuoteDetails details = new QuoteDetails(quoteId, GlobalConstants.State.NORMAL, operateTime, operateTime);
                BeanMapper.copy(d, details);
                detailsList.add(details);
            }
            this.quoteDetailsMapper.batchInsert(detailsList);
            this.quoteDetailHistoriesMapper.batchInsert(BeanMapper.mapList(detailsList, QuoteDetailHistories.class));
        }

        //采购单附件处理
        if (CollectionUtils.isNotEmpty(p.getAttIdList())) {
            this.attachmentService.updateBizIdByCondition(quoteId, DictConstants.QUOTE_HEADER, p.getAttIdList());
        }

        return token;
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

    /**
     * 报价单新增修改参数校验
     *
     * @param p 参数
     * @throws Exception
     */
    private void validateParam(QuoteParam p) throws Exception {

        if (StringUtils.isBlank(p.getCompanyName())) {
            throw new BaseException("报价单位不能为空");
        }

        if (StringUtils.isBlank(p.getContact())) {
            throw new BaseException("联系人不能为空");
        }

        if (StringUtils.isBlank(p.getVerifyCode())) {
            throw new BaseException("验证码不能为空");
        }

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