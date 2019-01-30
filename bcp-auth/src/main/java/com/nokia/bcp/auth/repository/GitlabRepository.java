package com.nokia.bcp.auth.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.nokia.bcp.auth.entity.DevopsGitlab;

public interface GitlabRepository extends CrudRepository<DevopsGitlab, Long> {

	@Query(value = "SELECT mgl.id, mgl.name, mgl.base_url_api, mgl.base_url_src, mgl.access_token FROM mgmt_group mg, mgmt_gitlab mgl WHERE mg.gitlab_id = mgl.id AND mg.id = ?1", nativeQuery = true)
	DevopsGitlab findByGroupId(long groupId);

}
