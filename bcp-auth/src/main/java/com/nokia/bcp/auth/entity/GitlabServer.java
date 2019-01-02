package com.nokia.bcp.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "mgmt_gitlab")
@Data
public class GitlabServer {

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

}
