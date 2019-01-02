package com.nokia.bcp.auth.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "mgmt_project")
@Data
public class DevopsProject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String projectDesc;

	private Long groupId;

	private int gitlabProjectId;

	private int docsProjectId;

}
