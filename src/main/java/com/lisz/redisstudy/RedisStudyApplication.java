package com.lisz.redisstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RedisStudyApplication {

	public static void main(String[] args) {
		//这里要用Springboot启动，读取配置
		ConfigurableApplicationContext ctx = SpringApplication.run(RedisStudyApplication.class, args);
		// 从容器里拿到对象之后使用
		TestRedis redis = ctx.getBean(TestRedis.class);
		redis.testRedis();
		redis.testRedis2();
		redis.testRedis3();
		redis.testRedis4();
		redis.testRedis5();
	}

}
