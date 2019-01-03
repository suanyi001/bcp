package com.nokia.bcp.auth.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

/**
 * DevOps 项目的用户
 * 
 * @author wangyueda
 *
 */
@Entity
@Table(name = "mgmt_project_user")
@Data
public class DevopsProjectUser {

	@EmbeddedId
	private DevopsProjectMemberId id;

	@Column(name = "member_role")
	private String memberRole;

	@Column(name = "member_type")
	private Integer memberType = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	@MapsId("projectId")
	@JsonBackReference
	private DevopsProject project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	@MapsId("memberId")
	@JsonBackReference
	private DevopsUser user;

}
