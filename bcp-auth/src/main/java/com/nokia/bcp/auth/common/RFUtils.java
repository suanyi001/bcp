package com.nokia.bcp.auth.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nokia.bcp.auth.entity.robot.RFTestReport;
import com.nokia.bcp.auth.entity.robot.RSTBlock;
import com.nokia.bcp.auth.entity.robot.RSTFile;
import com.nokia.bcp.auth.entity.robot.RSTInline;

public class RFUtils {

	public static List<String> parseTestCases(String source) throws IOException {
		List<String> result = new ArrayList<>();
		InputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
		List<String> lines = IOUtils.readLines(is, "UTF-8");
		boolean isTestCaseSection = false;
		for (String line : lines) {
			if (StringUtils.isBlank(line)) {
				continue;
			}

			if (line.startsWith("***")) {
				if ("*** Test Cases ***".equals(line)) {
					isTestCaseSection = true;
				} else {
					isTestCaseSection = false;
				}
				continue;
			}

			if (isTestCaseSection && StringUtils.isNotBlank(line.substring(0, 1))) {
				result.add(line);
			}
		}

		return result;
	}

	public static List<String> parseRstFile(RSTFile rstFile) throws IOException {
		List<String> result = new ArrayList<>();
		for (RSTBlock rstBlock : rstFile.getBlocks()) {
			if (!"code robotframework".equals(rstBlock.getClasses())) {
				continue;
			}

			boolean isTestCaseSection = false;
			for (RSTInline rstInline : rstBlock.getInline()) {
				String inlineClasses = rstInline.getClasses();
				String inlineContent = rstInline.getContent();

				// 判断是否是测试用例区域
				if ("generic heading".equals(inlineClasses)) {
					if ("*** Test Cases ***".equals(inlineContent)) {
						isTestCaseSection = true;
					} else {
						isTestCaseSection = false;
					}
					continue;
				}

				// 测试用例区域增加测试用例
				if (isTestCaseSection && "generic subheading".equals(inlineClasses)) {
					result.add(inlineContent);
				}
			}
		}

		return result;
	}

	public static RFTestReport parseReport(String source) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new XmlMapper();
		InputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
		RFTestReport report = objectMapper.readValue(is, RFTestReport.class);
		return report;
	}

}
