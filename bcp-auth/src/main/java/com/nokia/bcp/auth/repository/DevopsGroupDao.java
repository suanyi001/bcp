package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.DevopsGroup;

public interface DevopsGroupDao extends PagingAndSortingRepository<DevopsGroup, Long> {

}
