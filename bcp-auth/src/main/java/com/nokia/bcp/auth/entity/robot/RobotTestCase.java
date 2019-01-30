package com.nokia.bcp.auth.entity.robot;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "mgmt_robot_test_case")
@Data
@ToString(exclude = { "testSuite" })
@EqualsAndHashCode(callSuper = false)
public class RobotTestCase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 可选属性optional=false,表示测试套不能为空。删除测试用例，不影响测试套
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "test_suite_id") // 设置在测试用例表中的关联字段(外键)
	@JsonBackReference
	private RobotTestSuite testSuite;

	private String name;

	private String bugLevel;

}
