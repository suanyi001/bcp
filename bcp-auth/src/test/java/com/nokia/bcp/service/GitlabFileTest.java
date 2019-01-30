package com.nokia.bcp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.bcp.auth.entity.GitlabFile;

public class GitlabFileTest {

	public static void main(String[] args) throws JsonProcessingException {
		GitlabFile file = new GitlabFile();
		file.setId("8d09d57c51d9179b72bf9fcce9107bddc99dcaaf");
		file.setName("Install");
		file.setType("tree");
		file.setPath("Install");
		file.setMode("040000");
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(file));
	}

}
