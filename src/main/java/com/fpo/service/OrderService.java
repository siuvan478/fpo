package com.fpo.service;

import com.fpo.base.BaseException;
import com.fpo.constant.DictConstants;
import com.fpo.constant.GlobalConstants;
import com.fpo.mapper.AttachmentMapper;
import com.fpo.mapper.OrderDetailsMapper;
import com.fpo.mapper.OrderHeaderMapper;
import com.fpo.model.OrderDetails;
import com.fpo.model.OrderDetailsParam;
import com.fpo.model.OrderHeader;
import com.fpo.model.OrderParam;
import com.fpo.utils.BeanMapper;
import com.fpo.utils.DicUtil;
import com.fpo.utils.LoginUtil;
import com.fpo.vo.OrderMgtVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Resource
    private OrderHeaderMapper orderHeaderMapper;

    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    @Resource
    private AttachmentMapper attachmentMapper;

    @Resource
    private AttachmentService attachmentService;

    /**
     * 新增或修改报价单
     *
     * @param orderParam
     * @return
     * @throws Exception
     */
    @Transactional
    public Long addOrUpdate(OrderParam orderParam) throws Exception {

        //采购单ID
        Long orderId = null;

        //校验参数
        this.validateOrderInfo(orderParam);

        //新增
        if (orderParam.getId() == null) {
            //保存header
            OrderHeader header = BeanMapper.map(orderParam, OrderHeader.class);
            this.orderHeaderMapper.insert(header);
            //保存detail
            if (CollectionUtils.isNotEmpty(orderParam.getDetails())) {

                for (OrderDetailsParam d : orderParam.getDetails()) {
                    OrderDetails detail = BeanMapper.map(d, OrderDetails.class);
                    detail.setHeaderId(header.getId());
                    this.orderDetailsMapper.insert(detail);

                    //采购明细附件处理
                    if (d.getAttId() != null) {
                        this.attachmentService.updateBizIdByCondition(detail.getId(), DictConstants.PURCHASE_DETAIL, Collections.singletonList(d.getAttId()));
                    }
                }
            }

            orderId = header.getId();
        }
        //修改
        else {
            //更新采购单
            OrderHeader header = orderHeaderMapper.selectByPrimaryKey(orderParam.getId());
            if (header == null) {
                throw new BaseException("采购单不存在");
            }
            BeanMapper.copy(orderParam, header);
            header.setUpdateDate(new Date());
            this.orderHeaderMapper.updateByPrimaryKey(header);

            //删除采购明细
            this.orderDetailsMapper.deleteByHeaderId(orderParam.getId());

            //遍历插入采购明细
            if (!CollectionUtils.isEmpty(orderParam.getDetails())) {

                for (OrderDetailsParam d : orderParam.getDetails()) {
                    OrderDetails detail = BeanMapper.map(d, OrderDetails.class);
                    detail.setHeaderId(header.getId());
                    orderDetailsMapper.insert(detail);

                    //采购明细附件处理
                    if (d.getAttId() != null) {
                        this.attachmentService.updateBizIdByCondition(detail.getId(), DictConstants.PURCHASE_DETAIL, Collections.singletonList(d.getAttId()));
                    }
                }
            }

            orderId = header.getId();
        }

        //采购单附件处理
        if (CollectionUtils.isNotEmpty(orderParam.getAttIdList())) {
            this.attachmentService.updateBizIdByCondition(orderId, DictConstants.PURCHASE_HEADER, orderParam.getAttIdList());
        }

        return orderId;
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
     * 删除采购单
     *
     * @param p 采购单ID
     * @throws Exception
     */
    public void deleteOrder(OrderParam p) throws Exception {
        if (p.getId() == null) {
            throw new BaseException("参数异常");
        }
        OrderHeader orderHeader = orderHeaderMapper.selectByPrimaryKey(p.getId());
        if (orderHeader == null || !orderHeader.getUserId().equals(LoginUtil.getUserId())) {
            throw new BaseException("采购单不存在");
        }
        if (!orderHeader.getStatus().equals(GlobalConstants.State.STOP_QUOTE)) {
            throw new BaseException("请先暂停报价");
        }
        orderHeader.setUpdateDate(new Date());
        orderHeader.setStatus(GlobalConstants.State.DELETED);
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
        if (orderHeader == null || orderHeader.getStatus().equals(GlobalConstants.State.DELETED)) {
            throw new BaseException("采购单不存在");
        }
        OrderParam result = BeanMapper.map(orderHeader, OrderParam.class);
        //数据词典
        result.setInvoiceModeName(DicUtil.getDictValue(DictConstants.INVOICE_MODE_DICT_KEY, result.getInvoiceMode()));
        result.setQuoteModeName(DicUtil.getDictValue(DictConstants.QUOTE_MODE_DICT_KEY, result.getQuoteMode()));
        result.setPaymentModeName(DicUtil.getDictValue(DictConstants.QUOTE_MODE_DICT_KEY, result.getPaymentMode()));

        //采购明细
        result.setDetails(this.orderDetailsMapper.selectByHeaderId(headerId));

        // 附件
        Map<String, Object> map = Maps.newHashMap();
        map.put("bizId", headerId);
        map.put("bizType", DictConstants.PURCHASE_HEADER);
        result.setAttachmentList(this.attachmentMapper.selectListByBizIdAndType(map));
        return result;
    }

    /**
     * 获取采购单明细
     *
     * @param headerId
     * @return
     * @throws Exception
     */
    public List<OrderDetailsParam> getOrderDetails(Long headerId) throws Exception {
        return orderDetailsMapper.selectByHeaderId(headerId);
    }

    /**
     * 获取采购单Header信息
     *
     * @param headerId
     * @return
     * @throws Exception
     */
    public OrderHeader getOrderHeader(Long headerId) throws Exception {
        return orderHeaderMapper.selectByPrimaryKey(headerId);
    }

    /**
     * 采购管理列表
     *
     * @param pageNum    当前页数
     * @param pageSize   每页显示数
     * @param orderParam 条件
     * @return
     */
    public PageInfo<OrderMgtVO> pageQueryOrderInfo(Integer pageNum, Integer pageSize, OrderParam orderParam) {
        //开始分页
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<OrderMgtVO> orderMgtVOs = this.orderHeaderMapper.findOrderMgtVOsByCondition(orderParam);
        return new PageInfo<>(orderMgtVOs);
    }


    private void validateOrderInfo(OrderParam orderParam) throws Exception {
        if (StringUtils.isBlank(orderParam.getTitle())) throw new BaseException("采购单名称不能为空");
        if (!DicUtil.validate(DictConstants.INVOICE_MODE_DICT_KEY, orderParam.getInvoiceMode())) {
            throw new BaseException("发票方式有误");
        }
        if (!DicUtil.validate(DictConstants.QUOTE_MODE_DICT_KEY, orderParam.getQuoteMode())) {
            throw new BaseException("报价要求有误");
        }
        if (!DicUtil.validate(DictConstants.PAYMENT_MODE_DICT_KEY, orderParam.getPaymentMode())) {
            throw new BaseException("报价要求有误");
        }
        if (DictConstants.OTHER_PAY.equals(orderParam.getPaymentMode())) {
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