package com.nokia.bcp.auth.entity.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RFTestCase {
	@JacksonXmlProperty(isAttribute = true)
	private String id;

	@JacksonXmlProperty(isAttribute = true)
	private String name;

	private RFStatus status;
}
