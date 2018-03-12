package com.fpo.model;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageBean implements Serializable {
    private static final long serialVersionUID = -7944825761070482032L;

    private String templateKey; //模板key
    private JSONObject templateParam; //模板参数
    private List<String> phoneNumbers = new ArrayList<>();// 接受手机号
    private String outId; //业务方扩展字段

    public MessageBean() {
    }

    public void addPhoneNumber(String phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    public String getStringPhoneNumbers() {
        StringBuffer rs = new StringBuffer();
        if (phoneNumbers.size() > 0) {
            for (String phoneNumber : phoneNumbers) {
                if (StringUtils.isBlank(rs.toString())) {
                    rs.append(phoneNumber);
                } else {
                    rs.append(",").append(phoneNumber);
                }
            }
        }
        return rs.toString();
    }

    public MessageBean(String templateKey, JSONObject templateParam, String phoneNumber) {
        this.templateKey = templateKey;
        this.templateParam = templateParam;
        this.addPhoneNumber(phoneNumber);
    }

    public MessageBean(String templateKey, JSONObject templateParam, List<String> phoneNumbers) {
        this.templateKey = templateKey;
        this.templateParam = templateParam;
        this.phoneNumbers = phoneNumbers;
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public JSONObject getTemplateParam() {
        return templateParam;
    }

    public void setTemplateParam(JSONObject templateParam) {
        this.templateParam = templateParam;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }
}