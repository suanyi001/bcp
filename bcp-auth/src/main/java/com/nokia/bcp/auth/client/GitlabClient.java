package com.nokia.bcp.auth.client;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.Response;

public interface GitlabClient {

	/**
	 * 获取文件信息: </br>
	 * <ul>
	 * <li>{@value #PARAM_BRANCH}</li>
	 * </ul>
	 * 
	 * @param privateToken
	 *            access token
	 * @param projectId
	 *            project ID
	 * @param params
	 *            参数
	 * @return
	 */
	@Headers({ "PRIVATE-TOKEN: {privateToken}" })
	@RequestLine(value = "HEAD /projects/{projectId}/repository/files/{path}", decodeSlash = false)
	public Response getFile(@Param("privateToken") String privateToken, @Param("projectId") int projectId,
			@Param(value = "path", encoded = true) String path, @QueryMap Map<String, String> params);

	/**
	 * 获取文件列表: </br>
	 * <ul>
	 * <li>{@value #PARAM_PATH}</li>
	 * <li>{@value #PARAM_REF}</li>
	 * <li>{@value #PARAM_PAGE}</li>
	 * <li>{@value #PARAM_PER_PAGE}</li>
	 * <li>{@value #PARAM_RECURSIVE}</li>
	 * </ul>
	 * 
	 * @param privateToken
	 *            access token
	 * @param projectId
	 *            project ID
	 * @param params
	 *            参数
	 * @return
	 */
	@Headers({ "PRIVATE-TOKEN: {privateToken}" })
	@RequestLine("GET /projects/{projectId}/repository/tree")
	public Response listFiles(@Param("privateToken") String privateToken, @Param("projectId") int projectId,
			@QueryMap Map<String, String> params);

	/**
	 * 下载文件: </br>
	 * <ul>
	 * <li>{@value #PARAM_REF}</li>
	 * </ul>
	 * 
	 * @param privateToken
	 *            access token
	 * @param projectId
	 *            project ID
	 * @param path
	 *            file path
	 * @param params
	 *            参数
	 * @return
	 */
	@Headers({ "PRIVATE-TOKEN: {privateToken}" })
	@RequestLine(value = "GET /projects/{projectId}/repository/files/{path}/raw", decodeSlash = false)
	public Response downloadFile(@Param("privateToken") String privateToken, @Param("projectId") int projectId,
			@Param(value = "path", encoded = true) String path, @QueryMap Map<String, String> params);

	/**
	 * 创建新文件: </br>
	 * <ul>
	 * <li>{@value #PARAM_BRANCH}</li>
	 * <li>{@value #PARAM_ENCODING}</li>
	 * <li>{@value #PARAM_CONTENT}</li>
	 * <li>{@value #PARAM_COMMIT_MESSAGE}</li>
	 * </ul>
	 * 
	 * @param privateToken
	 *            access token
	 * @param projectId
	 *            project ID
	 * @param path
	 *            file path
	 * @param params
	 *            参数
	 * @return
	 */
	@Headers({ "PRIVATE-TOKEN: {privateToken}", "Content-Type: application/json" })
	@RequestLine(value = "POST /projects/{projectId}/repository/files/{path}", decodeSlash = false)
	public Response createFile(@Param("privateToken") String privateToken, @Param("projectId") int projectId,
			@Param(value = "path", encoded = true) String path, @RequestBody Map<String, String> params);

	/**
	 * 更新文件: </br>
	 * <ul>
	 * <li>{@value #PARAM_BRANCH}</li>
	 * <li>{@value #PARAM_ENCODING}</li>
	 * <li>{@value #PARAM_CONTENT}</li>
	 * <li>{@value #PARAM_COMMIT_MESSAGE}</li>
	 * </ul>
	 * 
	 * @param privateToken
	 *            access token
	 * @param projectId
	 *            project ID
	 * @param path
	 *            file path
	 * @param params
	 *            参数
	 * @return
	 */
	@Headers({ "PRIVATE-TOKEN: {privateToken}", "Content-Type: application/json" })
	@RequestLine(value = "PUT /projects/{projectId}/repository/files/{path}", decodeSlash = false)
	public Response updateFile(@Param("privateToken") String privateToken, @Param("projectId") int projectId,
			@Param(value = "path", encoded = true) String path, @RequestBody Map<String, String> params);

	/**
	 * 删除文件: </br>
	 * <ul>
	 * <li>{@value #PARAM_BRANCH}</li>
	 * <li>{@value #PARAM_ENCODING}</li>
	 * <li>{@value #PARAM_COMMIT_MESSAGE}</li>
	 * </ul>
	 * 
	 * @param privateToken
	 *            access token
	 * @param projectId
	 *            project ID
	 * @param path
	 *            file path
	 * @param params
	 *            参数
	 * @return
	 */
	@Headers({ "PRIVATE-TOKEN: {privateToken}", "Content-Type: application/json" })
	@RequestLine(value = "DELETE /projects/{projectId}/repository/files/{path}", decodeSlash = false)
	public Response deleteFile(@Param("privateToken") String privateToken, @Param("projectId") int projectId,
			@Param(value = "path", encoded = true) String path, @RequestBody Map<String, String> params);

	public static final String PARAM_NAME = "name";
	public static final String PARAM_PATH = "path";
	public static final String PARAM_REF = "ref";
	public static final String PARAM_PAGE = "page";
	public static final String PARAM_PER_PAGE = "per_page";
	public static final String PARAM_RECURSIVE = "recursive";
	public static final String PARAM_DESC = "description";
	public static final String PARAM_USER_ID = "user_id";
	public static final String PARAM_ACCESS_LEVEL = "access_level";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_USER_NAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_SCOPES = "scopes[]";
	public static final String PARAM_NAMESPACE_ID = "namespace_id";
	public static final String PARAM_CONTENT = "content";
	public static final String PARAM_COMMIT_MESSAGE = "commit_message";
	public static final String PARAM_BRANCH = "branch";
	public static final String PARAM_SKIP_CONFIRM = "skip_confirmation";
	public static final String PARAM_GROUP_ID = "group_id";
	public static final String PARAM_GROUP_ACCESS = "group_access";
	public static final String PARAM_ENCODING = "encoding";

}
