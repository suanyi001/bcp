package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.SysAuthority;

public interface AuthorityRepository extends PagingAndSortingRepository<SysAuthority, Long> {

	SysAuthority findByAuthorityCode(String authorityCode);

}
