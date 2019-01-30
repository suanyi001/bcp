package com.nokia.bcp.auth.entity.robot.report;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RFTestSuite {
	@JacksonXmlProperty(isAttribute = true)
	private String id;

	@JacksonXmlProperty(isAttribute = true)
	private String name;

	@JacksonXmlProperty(isAttribute = true)
	private String source;

	@JacksonXmlElementWrapper(useWrapping = false)
	private List<RFTestSuite> suite;

	@JacksonXmlElementWrapper(useWrapping = false)
	private List<RFTestCase> test;

	private RFStatus status;
}
