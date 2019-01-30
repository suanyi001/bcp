package com.nokia.bcp.auth.entity.robot.report;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RFStatus {
	// PASS/FAIL
	@JacksonXmlProperty(isAttribute = true)
	private String status;

	@JacksonXmlProperty(isAttribute = true)
	private String critical;

	@JacksonXmlProperty(isAttribute = true)
	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss.SSS", timezone = "GMT+8")
	private Date starttime;

	@JacksonXmlProperty(isAttribute = true)
	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss.SSS", timezone = "GMT+8")
	private Date endtime;

	// when FAIL
	@JacksonXmlText
	private String message;
}
