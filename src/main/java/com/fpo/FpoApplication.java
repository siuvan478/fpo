package com.fpo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fpo.mapper")//mapper扫描
public class FpoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FpoApplication.class, args);
    }
}
