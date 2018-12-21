package com.nokia.bcp.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.bcp.auth.entity.DbUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {

	@Autowired
	private RedisTemplate<String, DbUser> redisTemplate;

	@Test
	public void testRedis() {
		DbUser user = new DbUser();
		user.setId(1L);
		user.setAccount("admin");
		ValueOperations<String, DbUser> operations = redisTemplate.opsForValue();
		operations.set("key_user", user);
		// redisTemplate.opsForValue().set("key_user", "value_user");
		System.out.println(redisTemplate.opsForValue().get("key_user"));
	}

}
