package me.ooi.demo.testactiviti710_springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jun.zhao
 * @since 1.0
 */
@SpringBootApplication
@MapperScan("me.ooi.demo.testactiviti710_springboot.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}