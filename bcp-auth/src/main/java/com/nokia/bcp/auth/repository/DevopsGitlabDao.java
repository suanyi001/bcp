package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.DevopsGitlab;

public interface DevopsGitlabDao extends PagingAndSortingRepository<DevopsGitlab, Long> {

}
