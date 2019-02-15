package com.nokia.bcp.auth.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.bcp.auth.entity.DevopsGitlab;
import com.nokia.bcp.auth.entity.DevopsGroup;
import com.nokia.bcp.auth.entity.DevopsHarbor;
import com.nokia.bcp.auth.entity.DevopsJenkins;
import com.nokia.bcp.auth.entity.DevopsUser;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DevopsGroupDaoTest {

	@Autowired
	private DevopsGitlabDao gitlabDao;

	@Autowired
	private DevopsJenkinsDao jenkinsDao;

	@Autowired
	private DevopsHarborDao harborDao;

	@Autowired
	private DevopsGroupDao groupDao;

	@Autowired
	private DevopsUserDao userDao;

	@Test
	public void hello() throws JsonProcessingException {
		DevopsGitlab gitlab = gitlabDao.findById(1L).get();
		DevopsHarbor harbor = harborDao.findById(1L).get();
		DevopsJenkins jenkins = jenkinsDao.findById(1L).get();

		ObjectMapper mapper = new ObjectMapper();

		DevopsGroup group = new DevopsGroup();
		group.setGroupName("test-group1");
		group.setGroupDesc("测试租户1");
		group.setGitlabGroupId(1);
		group.setGitlab(gitlab);
		group.setHarbor(harbor);
		group.setJenkins(jenkins);
		System.out.println("Before insert: " + mapper.writeValueAsString(group));
		group = groupDao.save(group);
		System.out.println("After insert: " + mapper.writeValueAsString(group));
		group = groupDao.findById(group.getId()).get();
		System.out.println("Query out: " + mapper.writeValueAsString(group));

		DevopsUser user = new DevopsUser();
		user.setGitlabUserId(1);
		user.setGitlabToken("token1");
		user.setDevopsGroup(group);
		System.out.println("Before insert: " + mapper.writeValueAsString(user));
		user = userDao.save(user);
		System.out.println("After insert: " + mapper.writeValueAsString(user));
		user = userDao.findById(user.getId()).get();
		System.out.println("Query out: " + mapper.writeValueAsString(user));
		System.out.println("After insert a user: " + mapper.writeValueAsString(group));
		group = groupDao.findById(group.getId()).get();
		System.out.println("Requery out: " + mapper.writeValueAsString(group));
	}

}
