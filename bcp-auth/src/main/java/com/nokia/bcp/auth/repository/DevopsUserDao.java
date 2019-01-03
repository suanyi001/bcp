package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.DevopsUser;

public interface DevopsUserDao extends PagingAndSortingRepository<DevopsUser, Long> {

}
