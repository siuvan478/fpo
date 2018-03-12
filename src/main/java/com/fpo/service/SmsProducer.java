package com.fpo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 短信消息生产者
 */
@Component
public class SmsProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsProducer.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    public void send(String msg) {
        LOGGER.debug("消息入列-->" + msg);
        this.amqpTemplate.convertAndSend("smsQueue", msg);
    }

}