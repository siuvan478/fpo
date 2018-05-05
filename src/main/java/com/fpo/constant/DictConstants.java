package com.fpo.constant;


import com.fpo.annotation.Constant;
import com.fpo.annotation.DictGroup;

/**
 * 数据词典常量类
 */
@Constant
public class DictConstants {

    /**
     * 发票方式
     */
    public static final String INVOICE_MODE_DICT_KEY = "invoiceMode";
    @DictGroup(groupName = INVOICE_MODE_DICT_KEY, key = "0", value = "不用发票")
    public static final Integer NO_INVOICE = 0;
    @DictGroup(groupName = INVOICE_MODE_DICT_KEY, key = "1", value = "增值税普票")
    public static final Integer VAT_GENERAL_INVOICE = 1;
    @DictGroup(groupName = INVOICE_MODE_DICT_KEY, key = "2", value = "增值税专票")
    public static final Integer VAT_SPECIAL_INVOICE = 2;

    /**
     * 报价方式
     */
    public static final String QUOTE_MODE_DICT_KEY = "quoteMode";
    @DictGroup(groupName = QUOTE_MODE_DICT_KEY, key = "1", value = "报价含税")
    public static final Integer QUOTE_INCLUDE_TAX = 1;
    @DictGroup(groupName = QUOTE_MODE_DICT_KEY, key = "2", value = "报价含运费")
    public static final Integer QUOTE_INCLUDE_FREIGHT = 2;

    /**
     * 付款方式
     */
    public static final String PAYMENT_MODE_DICT_KEY = "paymentMode";
    @DictGroup(groupName = PAYMENT_MODE_DICT_KEY, key = "0", value = "其他")
    public static final Integer OTHER_PAY = 0;
    @DictGroup(groupName = PAYMENT_MODE_DICT_KEY, key = "1", value = "收货后付款")
    public static final Integer COD = 1;
    @DictGroup(groupName = PAYMENT_MODE_DICT_KEY, key = "2", value = "预付款")
    public static final Integer ADVANCE = 2;

    /**
     * 附件类型
     */
    public static final String BIZ_TYPE_DICT_KEY = "bizType";
    @DictGroup(groupName = BIZ_TYPE_DICT_KEY, key = "1", value = "采购附件")
    public static final Integer PURCHASE = 1;
    @DictGroup(groupName = BIZ_TYPE_DICT_KEY, key = "2", value = "报价附件")
    public static final Integer QUOTE = 2;

}