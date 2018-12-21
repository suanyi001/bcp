package com.nokia.bcp.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sys_user")
@Data
@EqualsAndHashCode(callSuper = false)
public class DbUser extends AbstractAuditingEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(unique = true, nullable = false)
	@Size(min = 1, max = 50)
	private String account;

	@NotNull
	@Column(unique = true, nullable = false)
	@Size(min = 1, max = 50)
	private String username;

	@NotNull
	@Column(length = 60)
	private String password;

	@Column(length = 100, unique = true, nullable = false)
	@Size(min = 8, max = 60)
	private String email;

	@ManyToMany(targetEntity = SysRole.class, fetch = FetchType.EAGER)
	@JoinTable(name = "sys_user_role", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	@BatchSize(size = 20)
	private Set<SysRole> roles = new HashSet<>();

}
