package com.lisz.redisstudy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TestRedis2 {

	@Autowired
	@Qualifier("getStringRedisTemplate") //@Quolifier里面写某个被@Configuration注解的类下面的某个被@Bean注解的方法名。@Qualifier用于强制按名字注入的情况
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired //下面的ObjectMapper类里面果然有一个无参的构造方法
	private ObjectMapper objectMapper;
	
	public void testRedis6() {
		Person p = new Person("wx", 18);
		HashMapper mapper = new Jackson2HashMapper(objectMapper, false);
		stringRedisTemplate.opsForHash().putAll("smith", mapper.toHash(p));
		Map<Object, Object> map2 = stringRedisTemplate.opsForHash().entries("smith");
		Person person = objectMapper.convertValue(map2, Person.class);
		System.out.println(person.getName() + ":" + person.getAge());
	}

	public void testPubSubSend() {
		stringRedisTemplate.convertAndSend("topic1", "Hello");//redis-cli去订阅，会发现发过来的消息
	}
	
	public void testPubSubReceive() {
		RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();
		connection.subscribe(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] pattern) {
				System.out.println(new String(message.getBody()));
			}
		}, "topic1".getBytes());
		
		while (true) {
			testPubSubSend();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
