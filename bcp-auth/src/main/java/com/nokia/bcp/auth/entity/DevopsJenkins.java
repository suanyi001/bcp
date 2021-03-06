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
@Table(name = "mgmt_jenkins")
@Data
public class DevopsJenkins {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "base_url")
	private String baseUrl;

	@Column(name = "auth_token")
	private String authToken;

	// 级联保存、更新、删除、刷新;延迟加载。当删除Jenkins服务器，会级联删除所有相关的租户
	// 拥有mappedBy注解的实体类为关系被维护端
	// mappedBy="devopsJenkins"中的devopsJenkins是租户使用的Jenkins服务器
	@OneToMany(mappedBy = "jenkins", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DevopsGroup> groupList;// 租户列表

}
