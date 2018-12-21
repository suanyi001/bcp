package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.DbUser;

public interface UserRepository extends PagingAndSortingRepository<DbUser, Long> {

	DbUser findByAccountOrEmail(String account, String email);

}
