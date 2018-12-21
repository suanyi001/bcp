package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.SysRole;

public interface RoleRepository extends PagingAndSortingRepository<SysRole, Long> {

	SysRole findByRoleCode(String roleCode);

}
