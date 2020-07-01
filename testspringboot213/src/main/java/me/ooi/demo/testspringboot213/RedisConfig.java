package me.ooi.demo.testspringboot213;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Configuration
public class RedisConfig {
	
	@Bean
	public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

}
