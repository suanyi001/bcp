package com.nokia.bcp.auth;

import java.util.HashSet;
import java.util.Set;
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
import com.nokia.bcp.auth.entity.SysRole;
import com.nokia.bcp.auth.repository.RoleRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleRepositoryTest {

	@Autowired
	private RoleRepository roleRepository;

	private String roleCode = "role";

	// @Before
	public void createData() {
		String roleCode = UUID.randomUUID().toString().substring(0, 10);
		for (int i = 0; i < 50; i++) {
			SysRole role = new SysRole();
			role.setRoleCode(roleCode + i);
			role.setRoleDesc("测试角色" + i);
			role.setLastModifiedBy(1L);
			role.setCreatedBy(1L);
			roleRepository.save(role);
		}
	}

	@Test
	public void findAll() throws Exception {
		SysRole role = roleRepository.findByRoleCode(roleCode);
		if (null == role) {
			role = new SysRole();
			role.setRoleCode(roleCode);
			role.setRoleDesc("测试角色");
			role.setCreatedBy(1L);
			role = roleRepository.save(role);
		} else {
			Set<SysAuthority> authorities = new HashSet<>();
			SysAuthority authority = new SysAuthority();
			authority.setId(18L);
			authorities.add(authority);
			role.setRoleDesc("角色1");
			role.setAuthorities(authorities);
			role = roleRepository.save(role);
		}

		Pageable pageable = PageRequest.of(0, 10, Direction.ASC, "roleCode");
		Iterable<SysRole> authorities = roleRepository.findAll(pageable);
		authorities.forEach(temp -> {
			System.out.println("findAll: " + temp);
		});
	}

}
