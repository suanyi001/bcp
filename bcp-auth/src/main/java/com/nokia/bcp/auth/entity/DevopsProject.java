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
 * DevOps 项目
 * 
 * @author wangyueda
 *
 */
@Entity
@Table(name = "mgmt_project")
@Data
public class DevopsProject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String projectName;

	private String projectDesc;

	private int gitlabProjectId;

	private int docsProjectId;

	// 可选属性optional=false,表示租户不能为空。删除项目，不影响租户
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "group_id") // 设置在项目表中的关联字段(外键)
	@JsonBackReference
	private DevopsGroup devopsGroup;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevopsProjectUser> userList;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DevopsProjectUserGroup> userGroupList;

	// @ManyToMany
	// @JoinTable(name = "mgmt_project_user", joinColumns = @JoinColumn(name =
	// "project_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
	// 1、关系维护端，负责多对多关系的绑定和解除
	// 2、@JoinTable注解的name属性指定关联表的名字，joinColumns指定外键的名字，关联到关系维护端(DevopsProject)
	// 3、inverseJoinColumns指定外键的名字，要关联的关系被维护端(DevopsUser)
	// 4、其实可以不使用@JoinTable注解，默认生成的关联表名称为主表表名+下划线+从表表名，
	// 即表名为user_authority
	// 关联到主表的外键名：主表名+下划线+主表中的主键列名,即user_id
	// 关联到从表的外键名：主表中用于关联的属性名+下划线+从表的主键列名,即authority_id
	// 主表就是关系维护端对应的表，从表就是关系被维护端对应的表
	// private List<DevopsUser> userList;

	// @ManyToMany
	// @JoinTable(name = "mgmt_project_user_group", joinColumns = @JoinColumn(name =
	// "project_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
	// private List<DevopsUserGroup> userGroupList;

}
