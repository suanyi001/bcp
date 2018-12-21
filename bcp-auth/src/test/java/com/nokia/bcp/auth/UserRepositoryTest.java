package com.nokia.bcp.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.bcp.auth.entity.DbUser;
import com.nokia.bcp.auth.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	// @Before
	public void createData() {
	}

	@Test
	public void findAll() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, Direction.ASC, "account");
		Iterable<DbUser> users = userRepository.findAll(pageable);
		users.forEach(temp -> {
			System.out.println("findAll: " + temp);
		});
	}

}
