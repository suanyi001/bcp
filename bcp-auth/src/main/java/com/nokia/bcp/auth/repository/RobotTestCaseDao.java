package com.nokia.bcp.auth.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.nokia.bcp.auth.entity.robot.RobotTestCase;

public interface RobotTestCaseDao extends PagingAndSortingRepository<RobotTestCase, Long> {

}
