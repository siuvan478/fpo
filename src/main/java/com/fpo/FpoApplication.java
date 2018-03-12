package com.fpo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.fpo.mapper")//mapper扫描
public class FpoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FpoApplication.class, args);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue("smsQueue");
    }
}
