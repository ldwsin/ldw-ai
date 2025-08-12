package com.yueyan.ldwaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.yueyan.ldwaicodemother.mapper")
public class LdwAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LdwAiCodeMotherApplication.class, args);
    }

}
