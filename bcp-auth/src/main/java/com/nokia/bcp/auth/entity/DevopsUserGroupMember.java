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
 * DevOps 用户组成员
 * 
 * @author wangyueda
 *
 */
@Entity
@Table(name = "mgmt_user_group_member")
@Data
public class DevopsUserGroupMember {

	@EmbeddedId
	private DevopsUserGroupMemberId id;

	@Column(name = "user_role")
	private String memberRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_group_id")
	@MapsId("userGroupId")
	@JsonBackReference
	private DevopsUserGroup userGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@MapsId("userId")
	@JsonBackReference
	private DevopsUser user;

}
