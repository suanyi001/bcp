package com.nokia.bcp.auth.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import com.nokia.bcp.auth.entity.QueueLimit;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.repository.QueueLimitDao;
import com.nokia.bcp.auth.service.QueueService;
import com.nokia.bcp.auth.utils.LocaleUtils;

@Service
public class QueueServiceImpl implements QueueService {

	private static final Logger Log = LoggerFactory.getLogger(QueueServiceImpl.class);
	private static final Map<String, QueueLimit> queueLimitMap = new HashMap<>();
	private static final Map<String, RedisAtomicLong> redisQueueMap = new HashMap<>();

	@Autowired
	private QueueLimitDao queueLimitDao;

	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	public ServiceResult<Long> incrementAndGet(String key) {
		ServiceResult<Long> result = new ServiceResult<>();

		try {
			QueueLimit queueLimit = null;
			boolean isNewQueue = false;
			if (!queueLimitMap.containsKey(key)) {
				queueLimit = queueLimitDao.findBySeqName(key);

				// 未使用过的序列
				if (null == queueLimit) {
					isNewQueue = true;
					QueueLimit tempQueueLimit = new QueueLimit();
					tempQueueLimit.setSeqName(key);
					queueLimit = updateQueueLimit(tempQueueLimit);
				}
				queueLimitMap.put(key, queueLimit);
			} else {
				queueLimit = queueLimitMap.get(key);
			}

			RedisAtomicLong redisQueue = null;
			if (redisQueueMap.containsKey(key)) {
				redisQueue = redisQueueMap.get(key);
			} else {
				redisQueue = new RedisAtomicLong(redisKeyPrefix + key, redisTemplate.getConnectionFactory());
				redisQueueMap.put(key, redisQueue);
			}

			long nextValue = 0L;
			do {
				// 需要重置 Redis 序列当前值，避免 Redis 停机造成的异常
				if (!isNewQueue && 0L == redisQueue.get()) {
					redisQueue.compareAndSet(0L, queueLimit.getMaxValue());
				}
				nextValue = redisQueue.incrementAndGet();
				// if ((!isNewQueue && 1 == curValue) || queueLimit.getLastMaxValue() >=
				// curValue) {
				// redisQueue.compareAndSet(curValue, queueLimit.getMaxValue());
				// curValue = redisQueue.incrementAndGet();
				// }

				// 满足条件时，更新数据库中的记录
				if (queueLimit.getUpdValue() <= nextValue) {
					QueueLimit tempQueueLimit = createQueueLimit(queueLimit);
					queueLimit = updateQueueLimit(tempQueueLimit);
					queueLimitMap.put(key, queueLimit);
				}
			} while (nextValue > queueLimit.getMaxValue());

			result.setData(nextValue);
			result.setSuccess(true);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_CONSUME_QUEUE));
		}

		return result;
	}

	private QueueLimit createQueueLimit(QueueLimit originalQueueLimit) {
		long lastUpdTime = originalQueueLimit.getLastUpdTime().getTime();
		long curTime = System.currentTimeMillis();
		long elapseSeconds = Math.max(1, (curTime - lastUpdTime) / 1000);
		long consumeNumbers = originalQueueLimit.getMaxValue() - originalQueueLimit.getLastMaxValue();
		long consumeRate = Math.max(1, consumeNumbers / elapseSeconds);
		long newMaxValue = originalQueueLimit.getLastMaxValue() + consumeRate * 60L;

		QueueLimit tempQueueLimit = new QueueLimit();
		tempQueueLimit.setId(originalQueueLimit.getId());
		tempQueueLimit.setSeqName(originalQueueLimit.getSeqName());
		tempQueueLimit.setUpdValue(newMaxValue - consumeRate * 6L);
		tempQueueLimit.setMaxValue(newMaxValue);
		tempQueueLimit.setLastUpdTime(new Date(curTime));
		tempQueueLimit.setLastMaxValue(originalQueueLimit.getMaxValue());
		tempQueueLimit.setVersion(originalQueueLimit.getVersion());

		return tempQueueLimit;
	}

	private QueueLimit updateQueueLimit(QueueLimit queueLimit) {
		QueueLimit tempQueueLimit = null;
		try {
			tempQueueLimit = queueLimitDao.saveAndFlush(queueLimit);
		} catch (StaleObjectStateException e) {
			Log.warn("", e);
			tempQueueLimit = queueLimitDao.findBySeqName(queueLimit.getSeqName());
		}
		Log.info(">>> QueueLimit: " + tempQueueLimit);
		return tempQueueLimit;
	}

	private static final String redisKeyPrefix = "BCP_QUEUES_";

}
