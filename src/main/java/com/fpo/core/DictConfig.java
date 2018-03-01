package com.fpo.core;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

@Component
@ConfigurationProperties(prefix = "dictConfig")
public class DictConfig {

    //发票方式 0=不用发票 1=增值税普票 2=增值税专票
    private Map<Integer, String> invoiceModeMap = new TreeMap<>();
    //报价要求 1=报价含税 2=报价含运费
    private Map<String, String> quoteModeMap = new TreeMap<>();
    //付款方式 0=其他 1=收货后付款 2=预付款
    private Map<Integer, String> paymentModeMap = new TreeMap<>();

    public Map<Integer, String> getInvoiceModeMap() {
        return invoiceModeMap;
    }

    public void setInvoiceModeMap(Map<Integer, String> invoiceModeMap) {
        this.invoiceModeMap = invoiceModeMap;
    }

    public Map<String, String> getQuoteModeMap() {
        return quoteModeMap;
    }

    public void setQuoteModeMap(Map<String, String> quoteModeMap) {
        this.quoteModeMap = quoteModeMap;
    }

    public Map<Integer, String> getPaymentModeMap() {
        return paymentModeMap;
    }

    public void setPaymentModeMap(Map<Integer, String> paymentModeMap) {
        this.paymentModeMap = paymentModeMap;
    }
}