package com.nokia.bcp.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.bcp.auth.entity.LdapUser;
import com.nokia.bcp.auth.repository.LdapUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LdapUserRepositoryTest {

	private String account = "wangyueda";

	@Autowired
	private LdapUserRepository userRepository;

	// @Before
	public void createData() {
	}

	@Test
	public void findAll() throws Exception {
		Iterable<LdapUser> persons = userRepository.findAll();
		persons.forEach(temp -> {
			System.out.println("findAll: " + temp);
		});

		LdapUser user = null;
		user = userRepository.findByAccount(account);
		String password;
		if (null == user) {
			System.out.println(">>>>>> create one new user ...");
			password = "oldPassword";
			user = new LdapUser();
			user.setUid(account);
			user.setAccount(account);
			user.setEmail(account + "@example.com");
			user.setUsername("测试用户");
			user.setPassword(password);
			// user.buildDn();
			userRepository.save(user);
			System.out.println(">>>>>> created one new user");
		} else {
			System.out.println(">>>>>> change user's password ...");
			password = "123987";
			// user.setPassword(password);
			userRepository.changePassword(account, password);
			System.out.println(">>>>>> changed user's password");
		}

		System.out.println(userRepository.authenticate(account + "@example.com", password));
		System.out.println(userRepository.authenticate(account, password));
	}

}
