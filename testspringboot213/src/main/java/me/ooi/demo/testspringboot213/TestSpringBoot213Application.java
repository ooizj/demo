package me.ooi.demo.testspringboot213;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author jun.zhao
 * @since 1.0
 */
@SpringBootApplication
@MapperScan("me.ooi.demo.testspringboot213.mapper")
@EnableWebMvc
public class TestSpringBoot213Application {

	public static void main(String[] args) {
		SpringApplication.run(TestSpringBoot213Application.class, args);
	}
	
}
