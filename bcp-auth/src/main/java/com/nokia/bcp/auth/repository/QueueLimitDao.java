package com.nokia.bcp.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nokia.bcp.auth.entity.QueueLimit;

public interface QueueLimitDao extends JpaRepository<QueueLimit, Long> {

	QueueLimit findBySeqName(String key);

}
