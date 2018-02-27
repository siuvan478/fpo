package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.mapper.OrderDetailsMapper;
import com.fpo.mapper.OrderHeaderMapper;
import com.fpo.model.OrderDetails;
import com.fpo.model.OrderDetailsParam;
import com.fpo.model.OrderHeader;
import com.fpo.model.OrderParam;
import com.fpo.utils.BeanMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class OrderService {

    @Resource
    private OrderHeaderMapper orderHeaderMapper;

    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    @Transactional
    public Long addOrUpdate(OrderParam orderParam) throws Exception {
        Long result = null;
        this.validateOrderInfo(orderParam);
        //新增
        if (orderParam.getId() == null) {
            OrderHeader header = BeanMapper.map(orderParam, OrderHeader.class);
            orderHeaderMapper.insert(header);
            if (CollectionUtils.isNotEmpty(orderParam.getDetails())) {
                for (OrderDetailsParam d : orderParam.getDetails()) {
                    OrderDetails detail = BeanMapper.map(d, OrderDetails.class);
                    detail.setHeaderId(header.getId());
                    orderDetailsMapper.insert(detail);
                }
            }
            result = header.getId();
        } else {
            OrderHeader header = orderHeaderMapper.selectByPrimaryKey(orderParam.getId());
            BeanMapper.copy(orderParam, header);
            header.setUpdateDate(new Date());
            orderHeaderMapper.updateByPrimaryKey(header);
            orderDetailsMapper.deleteByHeaderId(orderParam.getId());
            if (CollectionUtils.isNotEmpty(orderParam.getDetails())) {
                for (OrderDetailsParam d : orderParam.getDetails()) {
                    OrderDetails detail = BeanMapper.map(d, OrderDetails.class);
                    detail.setHeaderId(header.getId());
                    orderDetailsMapper.insert(detail);
                }
            }
            result = header.getId();
        }
        return result;
    }


    private void validateOrderInfo(OrderParam orderParam) throws Exception {
        if (StringUtils.isBlank(orderParam.getTitle())) throw new BaseException("采购单名称不能为空");
        if (!GlobalConstants.InvoiceMode.validate(orderParam.getInvoiceMode())) throw new BaseException("发票方式有误");
        if (!GlobalConstants.QuoteMode.validate(orderParam.getQuoteMode())) throw new BaseException("报价要求有误");
        if (!GlobalConstants.PaymentMode.validate(orderParam.getPaymentMode())) throw new BaseException("付款方式有误");
        if (GlobalConstants.PaymentMode.OTHER.equals(orderParam.getPaymentMode())) {
            if (StringUtils.isBlank(orderParam.getPaymentRemark())) {
                throw new BaseException("具体付款方式不能为空");
            } else {
                if (orderParam.getPaymentRemark().length() > 20) {
                    throw new BaseException("具体付款方式不能超过20个字符");
                }
            }
        }
        if (StringUtils.isBlank(orderParam.getReceiptAddress())) throw new BaseException("收货地址不能为空");
        if (StringUtils.isBlank(orderParam.getContact())) throw new BaseException("联系人不能为空");
        if (StringUtils.isBlank(orderParam.getContactInfo())) throw new BaseException("联系方式不能为空");

        if (CollectionUtils.isNotEmpty(orderParam.getDetails())) {
            for (OrderDetailsParam d : orderParam.getDetails()) {
                if (StringUtils.isBlank(d.getName())) throw new BaseException("产品名称不能为空");
                if (d.getQuantity() == null || d.getQuantity() < 1) throw new BaseException("数量填写有误");
                if (StringUtils.isBlank(d.getUnit())) throw new BaseException("单位不能为空");
                if (d.getUnit().length() > 20) {
                    throw new BaseException("单位不能超过20个字符");
                }
            }
        }
    }
}