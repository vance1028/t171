package com.market.scale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.market.scale.mapper")
public class FairScaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(FairScaleApplication.class, args);
    }
}
