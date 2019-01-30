package com.nokia.bcp.auth.entity.robot;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nokia.bcp.auth.entity.DevopsProject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "mgmt_robot_test_suite")
@Data
@ToString(exclude = { "devopsProject" })
@EqualsAndHashCode(callSuper = false)
public class RobotTestSuite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 可选属性optional=false,表示项目不能为空。删除测试套，不影响项目
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "project_id") // 设置在测试套表中的关联字段(外键)
	@JsonBackReference
	private DevopsProject devopsProject;

	private String filePath;

	private String fileMd5;

	private String bugLevel;

	private Date foundTime = new Date();

	private Date lastParseTime = new Date();

	// 级联保存、更新、删除、刷新;延迟加载。当删除测试套，会级联删除该测试套下的所有测试用例
	// 拥有mappedBy注解的实体类为关系被维护端
	// mappedBy="testSuite"中的testSuite是测试用例中的测试套属性
	@OneToMany(mappedBy = "testSuite", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RobotTestCase> testCaseList;// 测试用例列表

}
