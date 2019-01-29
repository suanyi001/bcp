package com.nokia.bcp.auth.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.bcp.auth.entity.GitlabFile;
import com.nokia.bcp.auth.entity.ServiceResult;

public interface GitlabFileService {

	/**
	 * 获取项目文档列表，文档由特定的 GitLab 项目管理。
	 * 
	 * @param projectId
	 *            项目 ID
	 * @param subpath
	 *            子目录，可以为空
	 * @return 指定子目录下的所有文件或文件夹
	 */
	ServiceResult<List<GitlabFile>> listFiles(int projectId, String subpath);

	/**
	 * 解析项目下RobotFramework测试套。
	 * 
	 * @param projectId
	 *            项目 ID
	 * @param subpath
	 *            子目录
	 * @return 解析指定子目录下的所有测试套文件
	 */
	ServiceResult<Void> parseRFFiles(int projectId, String subpath);

	/**
	 * 解析RobotFramework测试报告。
	 * 
	 * @param projectId
	 *            项目 ID
	 * @param report
	 *            报告内容
	 * @return 解析RobotFramework测试报告
	 */
	ServiceResult<Void> parseRFReport(int projectId, String report);

	/**
	 * 下载项目文档，只支持下载单个文件。
	 * 
	 * @param projectId
	 *            项目 ID
	 * @param path
	 *            文件全路径
	 * @return 文件内容
	 */
	ServiceResult<Resource> downloadFile(int projectId, String path);

	/**
	 * 创建或修改项目文档。
	 * 
	 * @param projectId
	 *            项目 ID
	 * @param path
	 *            文件全路径
	 * @param file
	 *            文件内容
	 * @return 修改后的文件全路径
	 */
	ServiceResult<String> uploadFile(int projectId, String path, MultipartFile file);

	/**
	 * 删除项目文档。
	 * 
	 * @param projectId
	 *            项目 ID
	 * @param path
	 *            文件全路径
	 * @return 删除的的文件全路径
	 */
	ServiceResult<String> deleteFile(int projectId, String path);

	String KEY_MSG_ERR_QUE_FILE = "MSG_ERR_QUE_FILE";
	String KEY_MSG_ERR_PARSE_TEST_SUITES = "MSG_ERR_PARSE_TEST_SUITES";
	String KEY_MSG_ERR_DEL_FILE = "MSG_ERR_DEL_FILE";
	String KEY_MSG_ERR_UPLOAD_FILE = "MSG_ERR_UPLOAD_FILE";
	String KEY_MSG_ERR_DOWNLOAD_FILE = "MSG_ERR_DOWNLOAD_FILE";
	String KEY_MSG_ERR_NO_PROJECT = "MSG_ERR_NO_PROJECT";
	String KEY_MSG_ERR_EMPTY_FILE = "MSG_ERR_EMPTY_FILE";

}
