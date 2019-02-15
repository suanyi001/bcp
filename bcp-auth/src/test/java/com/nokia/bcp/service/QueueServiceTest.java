package com.nokia.bcp.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nokia.bcp.auth.BcpAuth;
import com.nokia.bcp.auth.service.QueueService;

@SpringBootTest(classes = BcpAuth.class)
@RunWith(SpringRunner.class)
public class QueueServiceTest {

	@Autowired
	QueueService queueService;

	@Test
	public void findContainers() throws Exception {
		long startTime = System.currentTimeMillis();
		int seqCount = 10000;
		for (int i = 0; i < seqCount; i++) {
			queueService.incrementAndGet("TAB_ID_SYS_USER");
		}
		System.out.println("sequence: " + queueService.incrementAndGet("TAB_ID_SYS_USER"));
		System.out.println(
				"Created " + seqCount + " sequences, Elapse " + (System.currentTimeMillis() - startTime) + " ms");
	}

}
