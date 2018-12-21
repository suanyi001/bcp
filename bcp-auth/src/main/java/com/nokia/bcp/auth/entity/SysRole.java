package com.nokia.bcp.auth.entity;

import org.hibernate.annotations.BatchSize;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SysRole extends AbstractAuditingEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String roleCode;

	private String roleDesc;

	@ManyToMany(targetEntity = SysAuthority.class, fetch = FetchType.EAGER)
	@JoinTable(name = "sys_role_authority", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
			@JoinColumn(name = "authority_id") })
	@BatchSize(size = 20)
	private Set<SysAuthority> authorities = new HashSet<>();
}
