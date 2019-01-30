package com.nokia.bcp.auth.entity.robot.report;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSTBlock {

	@JacksonXmlProperty(isAttribute = true, localName = "classes")
	private String classes;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "inline")
	private List<RSTInline> inline;

}
