package com.nokia.bcp.auth.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
 * DevOps 用户组
 * 
 * @author wangyueda
 *
 */
@Entity
@Table(name = "mgmt_user_group")
@Data
public class DevopsUserGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name")
	private String userGroupName;

	private String userGroupDesc;

	private Integer gitlabGroupId;

	// 可选属性optional=false,表示租户不能为空。删除用户组，不影响租户
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "group_id") // 设置在用户组表中的关联字段(外键)
	@JsonBackReference
	private DevopsGroup devopsGroup;

	@OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevopsProjectUserGroup> projectList;

	@OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevopsUserGroupMember> userList;

	// @ManyToMany(mappedBy = "userGroupList")
	// private List<DevopsProject> projectList;

}
