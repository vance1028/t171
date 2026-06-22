package com.market.scale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.market.scale.mapper")
@EnableScheduling
public class FairScaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(FairScaleApplication.class, args);
    }
}
