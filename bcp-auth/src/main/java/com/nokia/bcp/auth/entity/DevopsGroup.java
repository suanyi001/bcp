package com.nokia.bcp.auth.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

/**
 * DevOps 租户
 * 
 * @author wangyueda
 *
 */
@Entity
@Table(name = "mgmt_group")
@Data
@ToString(exclude = { "gitlab", "jenkins", "harbor" })
public class DevopsGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String groupName;

	private String groupDesc;

	private Integer gitlabGroupId;

	private Integer managerId = 0;

	private Integer status;

	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date createDate = new Date();

	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date updateDate = new Date();

	// 级联保存、更新、删除、刷新;延迟加载。当删除租户，会级联删除该租户下的所有用户
	// 拥有mappedBy注解的实体类为关系被维护端
	// mappedBy="devopsGroup"中的devopsGroup是用户中的租户属性
	@OneToMany(mappedBy = "devopsGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DevopsUser> userList;// 用户列表

	// 级联保存、更新、删除、刷新;延迟加载。当删除租户，会级联删除该租户下的所有用户组
	// 拥有mappedBy注解的实体类为关系被维护端
	// mappedBy="devopsGroup"中的devopsGroup是用户组中的租户属性
	@OneToMany(mappedBy = "devopsGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DevopsUserGroup> userGroupList;// 用户组列表

	// 级联保存、更新、删除、刷新;延迟加载。当删除租户，会级联删除该租户下的所有项目
	// 拥有mappedBy注解的实体类为关系被维护端
	// mappedBy="devopsGroup"中的devopsGroup是项目中的租户属性
	@OneToMany(mappedBy = "devopsGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DevopsProject> projectList;// 项目列表

	// 可选属性optional=false,表示GitLab服务器不能为空。删除租户，不影响GitLab
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "gitlab_id") // 设置在租户表中的关联字段(外键)
	@JsonBackReference
	private DevopsGitlab gitlab;

	// 可选属性optional=false,表示Jenkins服务器不能为空。删除租户，不影响Jenkins
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "jenkins_id") // 设置在租户表中的关联字段(外键)
	@JsonBackReference
	private DevopsJenkins jenkins;

	// 可选属性optional=false,表示Harbor服务器不能为空。删除租户，不影响Harbor
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "harbor_id") // 设置在租户表中的关联字段(外键)
	@JsonBackReference
	private DevopsHarbor harbor;

}
