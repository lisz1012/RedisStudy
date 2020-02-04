package com.lisz.redisstudy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TestRedis2 {

	@Autowired
	@Qualifier("getStringRedisTemplate")
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public void testRedis6() {
		Person p = new Person("wx", 18);
		HashMapper mapper = new Jackson2HashMapper(objectMapper, false);
		stringRedisTemplate.opsForHash().putAll("smith", mapper.toHash(p));
		Map<Object, Object> map2 = stringRedisTemplate.opsForHash().entries("smith");
		Person person = objectMapper.convertValue(map2, Person.class);
		System.out.println(person.getName() + ":" + person.getAge());
	}

}
