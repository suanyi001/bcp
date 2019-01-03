package com.nokia.bcp.auth.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "mgmt_gitlab")
@Data
public class DevopsGitlab {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(name = "base_url_api")
	private String apiBaseUrl;

	@Column(name = "base_url_src")
	private String srcBaseUrl;

	@Column(name = "access_token")
	private String privateToken;

	// 级联保存、更新、删除、刷新;延迟加载。当删除Gitlab服务器，会级联删除所有相关的租户
	// 拥有mappedBy注解的实体类为关系被维护端
	// mappedBy="devopsGitlab"中的devopsGitlab是租户使用的Gitlab服务器
	@OneToMany(mappedBy = "gitlab", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DevopsGroup> groupList;// 租户列表

}
