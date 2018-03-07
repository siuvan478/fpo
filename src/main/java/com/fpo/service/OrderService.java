package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.base.GlobalConstants;
import com.fpo.core.DictConfig;
import com.fpo.mapper.OrderDetailsMapper;
import com.fpo.mapper.OrderHeaderMapper;
import com.fpo.model.OrderDetails;
import com.fpo.model.OrderDetailsParam;
import com.fpo.model.OrderHeader;
import com.fpo.model.OrderParam;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.LoginUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Resource
    private OrderHeaderMapper orderHeaderMapper;

    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    @Resource
    private DictConfig dictConfig;

    /**
     * 新增或修改报价单
     *
     * @param orderParam
     * @return
     * @throws Exception
     */
    @Transactional
    public Long addOrUpdate(OrderParam orderParam) throws Exception {
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

            return header.getId();
        }
        //修改
        else {
            OrderHeader header = orderHeaderMapper.selectByPrimaryKey(orderParam.getId());
            if (header == null) {
                throw new BaseException("采购单不存在");
            }
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

            return header.getId();
        }
    }

    /**
     * 暂停报价
     *
     * @param p 采购单ID
     * @throws Exception
     */
    public void stopQuote(OrderParam p) throws Exception {
        if (p.getId() == null) {
            throw new BaseException("参数异常");
        }
        OrderHeader orderHeader = orderHeaderMapper.selectByPrimaryKey(p.getId());
        if (orderHeader == null || !orderHeader.getUserId().equals(LoginUtil.getUserId())) {
            throw new BaseException("采购单不存在");
        }
        orderHeader.setUpdateDate(new Date());
        orderHeader.setStatus(GlobalConstants.State.STOP_QUOTE);
        orderHeaderMapper.updateByPrimaryKey(orderHeader);
    }

    /**
     * 预览采购单
     *
     * @param headerId
     * @return
     * @throws Exception
     */
    public OrderParam getOrderInfo(Long headerId) throws Exception {
        OrderHeader orderHeader = orderHeaderMapper.selectByPrimaryKey(headerId);
        if (orderHeader == null) {
            throw new BaseException("采购单不存在");
        }
        OrderParam result = BeanMapper.map(orderHeader, OrderParam.class);
        //数据词典
        result.setInvoiceModeName(dictConfig.getInvoiceModeMap().get(result.getInvoiceMode()));
        result.setQuoteModeName(dictConfig.getQuoteModeMap().get(result.getQuoteMode()));
        result.setInvoiceModeName(dictConfig.getInvoiceModeMap().get(result.getInvoiceMode()));
        final List<OrderDetails> orderDetails = orderDetailsMapper.selectByHeaderId(headerId);
        if (CollectionUtils.isNotEmpty(orderDetails)) {
            for (OrderDetails d : orderDetails) {
                final OrderDetailsParam odp = BeanMapper.map(d, OrderDetailsParam.class);
                odp.setOrderDetailId(d.getId());
                result.getDetails().add(odp);
            }
        }
        return result;
    }

    /**
     * 获取采购单明细
     *
     * @param headerId
     * @return
     * @throws Exception
     */
    public List<OrderDetails> getOrderDetails(Long headerId) throws Exception {
        return orderDetailsMapper.selectByHeaderId(headerId);
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