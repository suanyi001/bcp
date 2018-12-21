package com.nokia.bcp.auth;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.bcp.auth.entity.SysAuthority;
import com.nokia.bcp.auth.repository.AuthorityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthorityRepositoryTest {

	@Autowired
	private AuthorityRepository authorityRepository;

	private String authorityCode = "auth";

	public void createData() {
		String authrityCode = UUID.randomUUID().toString();
		for (int i = 0; i < 50; i++) {
			SysAuthority authority = new SysAuthority();
			authority.setAuthorityCode(authrityCode + i);
			authority.setAuthorityDesc("测试权限" + i);
			authority.setLastModifiedBy(1L);
			authority.setCreatedBy(1L);
			authorityRepository.save(authority);
		}
	}

	@Test
	public void findAll() throws Exception {
		SysAuthority authority = authorityRepository.findByAuthorityCode(authorityCode);
		if (null == authority) {
			authority = new SysAuthority();
			authority.setAuthorityCode(authorityCode);
			authority.setAuthorityDesc("鉴权");
			authority.setCreatedBy(1L);
			authority = authorityRepository.save(authority);
		} else {
			authority.setAuthorityDesc("鉴权1");
			authority = authorityRepository.save(authority);
		}

		Pageable pageable = PageRequest.of(0, 10, Direction.ASC, "authorityCode");
		Iterable<SysAuthority> authorities = authorityRepository.findAll(pageable);
		authorities.forEach(temp -> {
			System.out.println("findAll: " + temp);
		});
	}

}
