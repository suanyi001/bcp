package com.nokia.bcp.auth.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Version;

import lombok.Data;

@Entity
@Table(name = "bcp_queue_limit")
@Data
public class QueueLimit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "seq_name")
	private String seqName;

	@Column(name = "upd_value")
	private Long updValue = 900L;

	@Column(name = "max_value")
	private Long maxValue = 1000L;

	@Column(name = "last_upd_time")
	private Date lastUpdTime = new Date();

	@Column(name = "last_max_value")
	private Long lastMaxValue = 0L;

	@Version
	private int version = 1;

}
