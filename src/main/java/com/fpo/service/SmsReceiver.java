package com.fpo.service;

import com.fpo.model.MessageBean;
import com.fpo.utils.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 短信消费者
 */
@Component
@RabbitListener(queues = "smsQueue")
public class SmsReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsProducer.class);

    @Resource
    private SmsService smsService;

    @RabbitHandler
    public void receiveSms(String message) {
        LOGGER.debug("消息出列-->" + message);
        MessageBean messageBean = JsonMapper.nonEmptyMapper().fromJson(message, MessageBean.class);
        if (messageBean != null) {
            try {
                smsService.sendSms(messageBean.getStringPhoneNumbers(), messageBean.getTemplateParam(),
                        messageBean.getTemplateKey(), messageBean.getOutId());
                LOGGER.debug("短信消费成功");
            } catch (Exception e) {
                LOGGER.error("短信消费失败--->{}，", e.getMessage());
            }
        }
    }
}