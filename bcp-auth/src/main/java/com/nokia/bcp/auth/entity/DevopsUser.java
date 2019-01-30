package com.nokia.bcp.auth.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

/**
 * DevOps 用户
 * 
 * @author wangyueda
 *
 */
@Entity
@Table(name = "mgmt_user")
@Data
public class DevopsUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer gitlabUserId;

	private String gitlabToken;

	// 可选属性optional=false,表示租户不能为空。删除用户，不影响租户
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "group_id") // 设置在用户表中的关联字段(外键)
	@JsonBackReference
	private DevopsGroup devopsGroup;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevopsProjectUser> projectList;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevopsUserGroupMember> userGroupList;

	// @ManyToMany(mappedBy = "userList")
	// private List<DevopsProject> projectList;

}
