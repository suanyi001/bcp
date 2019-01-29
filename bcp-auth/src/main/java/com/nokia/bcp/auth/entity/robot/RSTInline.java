package com.nokia.bcp.auth.entity.robot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSTInline {
	@JacksonXmlProperty(isAttribute = true, localName = "classes")
	private String classes;

	@JacksonXmlText
	private String content;
}
