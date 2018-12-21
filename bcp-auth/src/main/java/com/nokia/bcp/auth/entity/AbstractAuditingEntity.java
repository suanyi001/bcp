package com.nokia.bcp.auth.entity;

import lombok.Data;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * Base abstract class for entities which will hold definitions for created,
 * last modified by and created, last modified by date.
 */
@MappedSuperclass
@Data
public abstract class AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@LastModifiedBy
	protected Long lastModifiedBy;

	@LastModifiedDate
	protected Date lastModifiedDate = new Date();

	@CreatedBy
	@Column(name = "created_by", nullable = false, updatable = false)
	protected Long createdBy;

	@CreatedDate
	@Column(name = "created_date", nullable = false, updatable = false)
	@NotNull
	protected Date createdDate = new Date();

}
