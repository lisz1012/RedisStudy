package com.lisz.redisstudy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TestRedis {
	
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate template;
	
	//上面的@Qualifier注解也可以用的种方式代替
	 @Autowired
	 private StringRedisTemplate stringRedisTemplate;
	 
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/*
	 * 测试高阶API
	 */
	public void testRedis() {
		// ForValue 就是 String类型的操作
		template.opsForValue().set("hello", "china");
		System.out.println(template.opsForValue().get("hello"));
	}
	
	/*
	 * 测试低阶API
	 */
	public void testRedis2() {
		RedisConnectionFactory redisConnectionFactory = template.getConnectionFactory();
		RedisConnection redisConnection = redisConnectionFactory.getConnection();
		redisConnection.set("hello02".getBytes(), "mashibing".getBytes()); //高低一对比就发现人累一点
		System.out.println(new String(redisConnection.get("hello02".getBytes())));
		/*
		    127.0.0.1:6379> keys *
			1) "\xac\xed\x00\x05t\x00\x05hello"
			2) "hello02"
			3) "hello"
			127.0.0.1:6379> get hello02
			"mashibing"
		 */
	}
	
	/*
	 * 测试hash
	 */
	public void testRedis3() {
		HashOperations<String, Object, Object> hashOperations = template.opsForHash();
		hashOperations.put("sean", "name", "zhozhilei");
		hashOperations.put("sean", "age", "22");	
		System.out.println(hashOperations.entries("sean"));
		System.out.println(hashOperations.get("sean", "name"));
		System.out.println(hashOperations.get("sean", "age"));
		
		/*
		 {name=zhozhilei, age=22}
		 zhozhilei
		 22
		 
		 127.0.0.1:6379> HGETALL sean
		1) "name"
		2) "zhozhilei"
		3) "age"
		4) "22"
		127.0.0.1:6379> HINCRBY sean age 1
		(integer) 23

		 */
	}
	
	/*
	 * 测试testRedis3的懒人模式 ----- 直接往里存对象
	 * https://docs.spring.io/spring-data/redis/docs/2.2.4.RELEASE/reference/html/#reference
	 */
	public void testRedis4() {
		Person p = new Person("zhangsan", 16);
		// Jackson2HashMapper一般用于异构系统
		HashMapper mapper = new Jackson2HashMapper(objectMapper, false);
		//Map<byte[], byte[]> map = mapper.toHash(p);
		//HashOperations<String, Object, Object> hashOperations = template.opsForHash();
		template.opsForHash().putAll("john", mapper.toHash(p));
		Map<Object, Object> map2 = template.opsForHash().entries("john");
		Person person = objectMapper.convertValue(map2, Person.class);
		System.out.println(person.getName() + ":" + person.getAge());
		/*
		 * 用
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate template;
	能存进去，但是会有乱码问题
		 */
	}
	
	public void testRedis5() {
		Person p = new Person("zhangsan", 16);
		HashMapper mapper = new Jackson2HashMapper(objectMapper, false);
		stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		stringRedisTemplate.opsForHash().putAll("john", mapper.toHash(p));
		Map<Object, Object> map2 = stringRedisTemplate.opsForHash().entries("john");
		Person person = objectMapper.convertValue(map2, Person.class);
		System.out.println(person.getName() + ":" + person.getAge());
		
		/*
		127.0.0.1:6379> hgetall john
		1) "name"
		2) "\"zhangsan\""   <--- 转义字符会被剔除掉，不是错误
		3) "age"
		4) "16"
		
		127.0.0.1:6379> HINCRBY john age 1
		(integer) 17
		127.0.0.1:6379> hgetall john
		1) "name"
		2) "\"zhangsan\""
		3) "age"
		4) "17"
		 */
	}
}
