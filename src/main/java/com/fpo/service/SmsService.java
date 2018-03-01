package com.fpo.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fpo.base.BaseException;
import com.fpo.utils.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "smsConfig")
public class SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    //产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    /**
     * 发送短信验证码
     *
     * @param phoneNumbers 手机号
     * @param tempParam    模板参数
     * @param templateCode 模板代码
     * @param outId        业务ID
     */
    public void sendSms(String phoneNumbers, JSONObject tempParam, String templateCode, String outId) throws Exception {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:待发送手机号
        request.setPhoneNumbers(phoneNumbers);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        if (tempParam != null) {
            request.setTemplateParam(tempParam.toJSONString());
        }
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(outId);
        //hint 此处可能会抛出异常，注意catch
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            SendSmsResponse resp = acsClient.getAcsResponse(request);
            if (resp.getCode() != null && !resp.getCode().equals("OK")) {
                LOGGER.error("短信发送失败, code={}, message={}", resp.getCode(), resp.getMessage());
                throw new BaseException("resp.getMessage()");
            } else {
                LOGGER.info("短信发送成功, INFO={}", JsonMapper.nonEmptyMapper().toJson(resp));
            }
        } catch (ClientException e) {
            e.printStackTrace();
            LOGGER.error("短信发送失败, phone={}, tempParam={}", phoneNumbers, tempParam == null ? null : tempParam.toJSONString());
        }
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }
}