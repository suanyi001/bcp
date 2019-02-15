package com.nokia.bcp.auth.service;

import com.nokia.bcp.auth.entity.ServiceResult;

public interface QueueService {

	ServiceResult<Long> incrementAndGet(String key);

	String KEY_MSG_ERR_CONSUME_QUEUE = "MSG_ERR_CONSUME_QUEUE";

}
