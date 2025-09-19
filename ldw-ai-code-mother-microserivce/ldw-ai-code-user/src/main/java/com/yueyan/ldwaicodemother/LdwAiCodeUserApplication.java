package com.yueyan.ldwaicodemother;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.yueyan.ldwaicodemother.mapper")
@ComponentScan("com.yueyan")
//@EnableDubbo
public class LdwAiCodeUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(LdwAiCodeUserApplication.class, args);
    }
}
