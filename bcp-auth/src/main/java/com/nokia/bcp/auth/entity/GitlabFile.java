package com.nokia.bcp.auth.entity;

import lombok.Data;

@Data
public class GitlabFile implements Comparable<GitlabFile> {

	private String id;

	private String name;

	// tree or blob
	private String type;

	private String path;

	private String mode;

	/**
	 * 首先根据类型比较，如果相同再根据文件名比较。</br>
	 * 文件夹排在前面，文件排在后面。</br>
	 */
	@Override
	public int compareTo(GitlabFile o) {
		int result = 0;
		if (null != type) {
			result = -type.compareTo(o.getType());
		}
		if (0 == result && null != name) {
			result = name.compareTo(o.getName());
		}
		return result;
	}

}
