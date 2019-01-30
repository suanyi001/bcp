package com.nokia.bcp.auth.entity.robot.report;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "document")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSTFile {
	@JacksonXmlProperty(isAttribute = true, localName = "source")
	private String source;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "literal_block")
	private List<RSTBlock> blocks;

}
