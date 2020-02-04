package com.lisz.redisstudy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;


@Configuration
public class MyTemplate {
	@Bean
	public StringRedisTemplate getStringRedisTemplate (RedisConnectionFactory fc) {
		StringRedisTemplate template = new StringRedisTemplate(fc);
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		return template;
	}
}
