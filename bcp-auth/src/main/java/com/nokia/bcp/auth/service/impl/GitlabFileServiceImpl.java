package com.nokia.bcp.auth.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.bcp.auth.client.GitlabClient;
import com.nokia.bcp.auth.client.GitlabClientBuilder;
import com.nokia.bcp.auth.entity.DevopsProject;
import com.nokia.bcp.auth.entity.GitlabFile;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.repository.DevopsProjectRepository;
import com.nokia.bcp.auth.service.GitlabFileService;
import com.nokia.bcp.auth.utils.LocaleUtils;

import feign.Response;
import feign.Util;

@Service
public class GitlabFileServiceImpl implements GitlabFileService {

	private static final Logger Log = LoggerFactory.getLogger(GitlabFileServiceImpl.class);

	@Autowired
	private DevopsProjectRepository projectRepository;

	@Value("${bcp.upload.tempPath:}")
	private String tempPath;

	public ServiceResult<List<GitlabFile>> listFiles(int projectId, String subpath) {
		ServiceResult<List<GitlabFile>> result = new ServiceResult<>();
		List<GitlabFile> files = new ArrayList<>();
		Response response = null;
		try {
			Optional<DevopsProject> projectOptional = projectRepository.findById((long) projectId);
			if (!projectOptional.isPresent()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NO_PROJECT));
				return result;
			}
			long groupId = projectOptional.get().getGroupId();
			int docsProjectId = projectOptional.get().getDocsProjectId();

			Map<String, String> params = new HashMap<String, String>();
			if (StringUtils.isNotBlank(subpath)) {
				params.put(GitlabClient.PARAM_PATH, subpath.trim());
			}

			GitlabClient gitlabClient = GitlabClientBuilder.getOrCreateByGroup(groupId);
			String token = GitlabClientBuilder.getTokenByGroup(groupId);

			while (true) {
				response = gitlabClient.listFiles(token, docsProjectId, params);
				String respJson = Util.toString(response.body().asReader());
				ObjectMapper mapper = new ObjectMapper();
				List<GitlabFile> fileList = mapper.readValue(respJson, new TypeReference<List<GitlabFile>>() {
				});
				files.addAll(fileList);

				// GitLab 默认 20 条记录分页
				// 根据响应头 X-Next-Page 有值判断是否还有其他数据
				params.remove(GitlabClient.PARAM_PAGE);
				response.headers().getOrDefault(HEADER_X_NEXT_PAGE, empryStringCollection).forEach(nextPageHeader -> {
					if (StringUtils.isNotBlank(nextPageHeader)) {
						params.put(GitlabClient.PARAM_PAGE, nextPageHeader.trim());
					}
				});

				if (!params.containsKey(GitlabClient.PARAM_PAGE)) {
					break;
				}

				IOUtils.closeQuietly(response);
			}

			// 将结果排序
			Collections.sort(files);

			result.setSuccess(true);
			result.setData(files);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_FILE));
		} finally {
			IOUtils.closeQuietly(response);
		}
		return result;
	}

	public ServiceResult<Resource> downloadFile(int projectId, String path) {
		ServiceResult<Resource> result = new ServiceResult<>();
		Response response = null;
		try {
			Optional<DevopsProject> projectOptional = projectRepository.findById((long) projectId);
			if (!projectOptional.isPresent()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NO_PROJECT));
				return result;
			}
			long groupId = projectOptional.get().getGroupId();
			int docsProjectId = projectOptional.get().getDocsProjectId();

			GitlabClient gitlabClient = GitlabClientBuilder.getOrCreateByGroup(groupId);
			String token = GitlabClientBuilder.getTokenByGroup(groupId);

			Map<String, String> params = new HashMap<String, String>();
			params.put(GitlabClient.PARAM_REF, "master");
			String encodedPath = URLEncoder.encode(path, "UTF-8");
			Log.info(">>>>> encodedPath: " + encodedPath);
			// response = gitlabClient.downloadFile(token, docsProjectId,
			// URLEncoder.encode(path,
			// "UTF-8"), params);
			response = gitlabClient.downloadFile(token, docsProjectId, encodedPath, params);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
			InputStream respIs = response.body().asInputStream();
			byte[] temp = new byte[1024];
			int readCnt = -1;
			while (-1 != (readCnt = respIs.read(temp))) {
				baos.write(temp, 0, readCnt);
			}
			Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));

			result.setSuccess(true);
			result.setData(resource);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_FILE));
		} finally {
			IOUtils.closeQuietly(response);
		}
		return result;
	}

	public ServiceResult<String> uploadFile(int projectId, String path, MultipartFile file) {
		ServiceResult<String> result = new ServiceResult<>();
		Response response = null;
		File tempFile = null;
		FileInputStream fio = null;
		try {
			if (null == file || file.isEmpty()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_EMPTY_FILE));
				return result;
			}

			tempFile = new File(tempPath, file.getOriginalFilename());
			file.transferTo(tempFile);

			Optional<DevopsProject> projectOptional = projectRepository.findById((long) projectId);
			if (!projectOptional.isPresent()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NO_PROJECT));
				return result;
			}
			long groupId = projectOptional.get().getGroupId();
			int docsProjectId = projectOptional.get().getDocsProjectId();

			GitlabClient gitlabClient = GitlabClientBuilder.getOrCreateByGroup(groupId);
			String token = GitlabClientBuilder.getTokenByGroup(groupId);

			Map<String, String> params = new HashMap<String, String>();
			params.put(GitlabClient.PARAM_REF, "master");

			String encodedPath = URLEncoder.encode(path, "UTF-8");
			Log.info(">>>>> encodedPath: " + encodedPath);
			response = gitlabClient.getFile(token, docsProjectId, encodedPath, params);
			int statusCode = response.status();
			Log.info(">>>>> statusCode: " + statusCode);
			boolean exists = 200 <= statusCode && 300 > statusCode;
			IOUtils.closeQuietly(response);

			fio = new FileInputStream(tempFile);
			byte[] tempContent = IOUtils.toByteArray(fio);

			params.clear();
			params.put(GitlabClient.PARAM_BRANCH, "master");
			params.put(GitlabClient.PARAM_ENCODING, "base64");
			params.put(GitlabClient.PARAM_CONTENT, Base64.encodeBase64String(tempContent));
			if (exists) {
				params.put(GitlabClient.PARAM_COMMIT_MESSAGE, "Update file " + path);
				response = gitlabClient.updateFile(token, docsProjectId, encodedPath, params);
			} else {
				params.put(GitlabClient.PARAM_COMMIT_MESSAGE, "Create file " + path);
				response = gitlabClient.createFile(token, docsProjectId, encodedPath, params);
			}
			Log.info(response.reason());

			result.setSuccess(true);
			result.setData(tempFile.getAbsolutePath());
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_FILE));
		} finally {
			IOUtils.closeQuietly(fio);
			IOUtils.closeQuietly(response);
			tempFile.delete();
		}
		return result;
	}

	public ServiceResult<String> deleteFile(int projectId, String path) {
		ServiceResult<String> result = new ServiceResult<>();
		Response response = null;
		try {
			Optional<DevopsProject> projectOptional = projectRepository.findById((long) projectId);
			if (!projectOptional.isPresent()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NO_PROJECT));
				return result;
			}
			long groupId = projectOptional.get().getGroupId();
			int docsProjectId = projectOptional.get().getDocsProjectId();

			GitlabClient gitlabClient = GitlabClientBuilder.getOrCreateByGroup(groupId);
			String token = GitlabClientBuilder.getTokenByGroup(groupId);

			Map<String, String> params = new HashMap<String, String>();
			params.put(GitlabClient.PARAM_REF, "master");
			params.put(GitlabClient.PARAM_COMMIT_MESSAGE, "Delete file " + path);
			String encodedPath = URLEncoder.encode(path, "UTF-8");
			Log.info(">>>>> encodedPath: " + encodedPath);
			response = gitlabClient.deleteFile(token, docsProjectId, encodedPath, params);

			result.setSuccess(true);
			result.setData(path);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_DEL_FILE));
		} finally {
			IOUtils.closeQuietly(response);
		}
		return result;
	}

	private static final String HEADER_X_NEXT_PAGE = "X-Next-Page";
	private static final Collection<String> empryStringCollection = new ArrayList<>(0);

}
