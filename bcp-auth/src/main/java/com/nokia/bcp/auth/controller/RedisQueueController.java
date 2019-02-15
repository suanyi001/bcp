package com.nokia.bcp.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.bcp.auth.entity.QueueResult;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.service.QueueService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(consumes = "application/json", produces = "application/json", value = "序列号接口")
public class RedisQueueController extends BaseController {

	@Autowired
	private QueueService queueService;

	@GetMapping(path = "/queues/{key}", produces = "application/json")
	@ApiOperation("获取指定序列当前值")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public QueueResult getAndIncrement(@PathVariable(name = "key", required = true) String key) {
		ServiceResult<Long> serviceResult = queueService.incrementAndGet(key);
		if (serviceResult.isSuccess()) {
			QueueResult result = new QueueResult(key, serviceResult.getData());
			return result;
		} else {
			setRespStatus(501, serviceResult.getMessage());
			return null;
		}
	}

}
