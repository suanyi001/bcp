package com.nokia.bcp.auth.entity.robot.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "robot")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RFTestReport {
	private RFTestSuite suite;
}
