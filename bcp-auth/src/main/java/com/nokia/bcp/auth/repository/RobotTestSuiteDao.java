package com.nokia.bcp.auth.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.DevopsProject;
import com.nokia.bcp.auth.entity.robot.RobotTestSuite;

public interface RobotTestSuiteDao extends PagingAndSortingRepository<RobotTestSuite, Long> {

	List<RobotTestSuite> findByDevopsProject(DevopsProject devopsProject);

}
