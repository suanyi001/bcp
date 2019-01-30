package com.nokia.bcp.auth.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nokia.bcp.auth.client.GitlabClient;
import com.nokia.bcp.auth.client.GitlabClientBuilder;
import com.nokia.bcp.auth.entity.DevopsProject;
import com.nokia.bcp.auth.entity.GitlabFile;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.entity.robot.RobotTestCase;
import com.nokia.bcp.auth.entity.robot.RobotTestSuite;
import com.nokia.bcp.auth.entity.robot.report.RFTestCase;
import com.nokia.bcp.auth.entity.robot.report.RFTestReport;
import com.nokia.bcp.auth.entity.robot.report.RFTestSuite;
import com.nokia.bcp.auth.repository.DevopsProjectRepository;
import com.nokia.bcp.auth.repository.RobotTestCaseDao;
import com.nokia.bcp.auth.repository.RobotTestSuiteDao;
import com.nokia.bcp.auth.service.GitlabFileService;
import com.nokia.bcp.auth.utils.LocaleUtils;
import com.nokia.bcp.auth.utils.RFUtils;

import feign.Response;
import feign.Util;
import lombok.Data;

@Service
public class GitlabFileServiceImpl implements GitlabFileService {

	private static final Logger Log = LoggerFactory.getLogger(GitlabFileServiceImpl.class);

	@Autowired
	private RobotTestSuiteDao robotTestSuiteDao;

	@Autowired
	private RobotTestCaseDao robotTestCaseDao;

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
			long groupId = projectOptional.get().getDevopsGroup().getId();
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

	public ServiceResult<Void> parseRFFiles(int projectId, String subpath) {
		ServiceResult<Void> result = new ServiceResult<>();
		List<GitlabFile> gitlabFiles = new ArrayList<>();
		Response response = null;
		try {
			// 查询项目信息
			Optional<DevopsProject> projectOptional = projectRepository.findById((long) projectId);
			if (!projectOptional.isPresent()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NO_PROJECT));
				return result;
			}
			long groupId = projectOptional.get().getDevopsGroup().getId();
			int gitlabProjectId = projectOptional.get().getGitlabProjectId();

			GitlabClient gitlabClient = GitlabClientBuilder.getOrCreateByGroup(groupId);
			String token = GitlabClientBuilder.getTokenByGroup(groupId);

			// 遍历指定目录下所有文件或文件夹
			gitlabFiles = listGitlabFiles(gitlabClient, token, gitlabProjectId, subpath, true);
			Map<String, GitlabFile> rfFileMap = new HashMap<>();
			for (GitlabFile gitlabFile : gitlabFiles) {
				String filePath = gitlabFile.getPath();
				// 过滤所有测试用例文件
				if ("blob".equals(gitlabFile.getType())) {
					File tempFile = new File(filePath);
					String filename = tempFile.getName().toLowerCase();
					Log.info(">>>>>> filename: " + filename);
					if (!filename.startsWith("__") && (filename.endsWith(".robot") || filename.endsWith(".txt"))) {
						rfFileMap.put(gitlabFile.getPath(), gitlabFile);
					}
				}
			}

			// 查询项目下所有测试套
			List<RobotTestSuite> testSuiteList = robotTestSuiteDao.findByDevopsProject(projectOptional.get());
			Map<String, RobotTestSuite> currentTestSuites = new HashMap<>();
			List<RobotTestSuite> deletedTestSuites = new ArrayList<>();
			List<RobotTestCase> deletedTestCases = new ArrayList<>();

			// 删除已经不存在的测试套
			for (RobotTestSuite testSuite : testSuiteList) {
				String filePath = testSuite.getFilePath();
				if (!rfFileMap.containsKey(filePath)) {
					deletedTestSuites.add(testSuite);
				} else {
					currentTestSuites.put(filePath, testSuite);
				}
			}

			for (GitlabFile gitlabFile : rfFileMap.values()) {
				String filePath = gitlabFile.getPath();

				ByteArrayOutputStream baos = downloadFromGitlab(gitlabClient, token, gitlabProjectId, filePath);
				byte[] bytes = baos.toByteArray();
				String md5 = DigestUtils.md5Hex(bytes);
				RobotTestSuite testSuite = null;
				boolean needParse = false;
				if (currentTestSuites.containsKey(filePath)) {
					// 该文件之前分析过，需要判断是否有变化
					testSuite = currentTestSuites.get(filePath);
					if (!md5.equals(testSuite.getFileMd5())) {
						// testSuite.getTestCaseList().clear();
						// robotTestSuiteDao.save(testSuite);
						needParse = true;
					}
				} else {
					// 该文件是新增的测试套
					List<RobotTestCase> testCaseList = new ArrayList<>();
					testSuite = new RobotTestSuite();
					testSuite.setDevopsProject(projectOptional.get());
					testSuite.setFilePath(filePath);
					testSuite.setBugLevel(dftBugLevel);
					testSuite.setTestCaseList(testCaseList);
					currentTestSuites.put(filePath, testSuite);
					needParse = true;
				}

				if (needParse) {
					String bugLevel = testSuite.getBugLevel();
					List<RobotTestCase> testCaseList = testSuite.getTestCaseList();
					String content = new String(baos.toByteArray(), "UTF-8");
					Set<String> testCaseNames = new HashSet<>();
					testCaseNames.addAll(RFUtils.parseTestCases(content));

					// 当前的测试用例
					Map<String, RobotTestCase> currentTestCases = new HashMap<>();
					for (RobotTestCase testCase : testCaseList) {
						String testCaseName = testCase.getName();
						if (testCaseNames.contains(testCaseName)) {
							currentTestCases.put(testCaseName, testCase);
						} else {
							deletedTestCases.add(testCase);
							Log.info("Deleted test case: " + testCase);
						}
					}

					for (String testCaseName : testCaseNames) {
						if (currentTestCases.containsKey(testCaseName)) {
							continue;
						}
						RobotTestCase testCase = new RobotTestCase();
						testCase.setTestSuite(testSuite);
						testCase.setName(testCaseName);
						testCase.setBugLevel(bugLevel);
						currentTestCases.put(testCaseName, testCase);
					}

					testCaseList.clear();
					testCaseList.addAll(currentTestCases.values());

					testSuite.setFileMd5(md5);
					testSuite.setLastParseTime(new Date());
				}
			}

			robotTestCaseDao.deleteAll(deletedTestCases);
			robotTestSuiteDao.deleteAll(deletedTestSuites);
			robotTestSuiteDao.saveAll(currentTestSuites.values());

			result.setSuccess(true);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_PARSE_TEST_SUITES));
		} finally {
			IOUtils.closeQuietly(response);
		}
		return result;
	}

	public ServiceResult<Void> changeSuiteBugLevel(int testSuiteId, String bugLevel, boolean recursive) {
		ServiceResult<Void> result = new ServiceResult<>();

		if (!validBugLevels.contains(bugLevel)) {
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_INVALID_BUG_LEVEL));
			return result;
		}

		try {
			Optional<RobotTestSuite> testSuiteOptional = robotTestSuiteDao.findById((long) testSuiteId);
			if (testSuiteOptional.isPresent()) {
				RobotTestSuite testSuite = testSuiteOptional.get();
				if (recursive) {
					for (RobotTestCase testCase : testSuite.getTestCaseList()) {
						testCase.setBugLevel(bugLevel);
					}
				}
				testSuite.setBugLevel(bugLevel);
				robotTestSuiteDao.save(testSuite);
				result.setSuccess(true);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_NO_TEST_SUITE));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_UPD_TEST_SUITE));
		}
		return result;
	}

	public ServiceResult<Void> changeCaseBugLevel(int testCaseId, String bugLevel) {
		ServiceResult<Void> result = new ServiceResult<>();

		if (!validBugLevels.contains(bugLevel)) {
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_INVALID_BUG_LEVEL));
			return result;
		}

		try {
			Optional<RobotTestCase> testCaseOptional = robotTestCaseDao.findById((long) testCaseId);
			if (testCaseOptional.isPresent()) {
				RobotTestCase testCase = testCaseOptional.get();
				testCase.setBugLevel(bugLevel);
				robotTestCaseDao.save(testCase);
				result.setSuccess(true);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_NO_TEST_CASE));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_UPD_TEST_CASE));
		}
		return result;
	}

	public ServiceResult<Void> parseRFReport(int projectId, String report) {
		ServiceResult<Void> result = new ServiceResult<>();
		Response response = null;
		try {
			// 查询项目信息
			Optional<DevopsProject> projectOptional = projectRepository.findById((long) projectId);
			if (!projectOptional.isPresent()) {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NO_PROJECT));
				return result;
			}

			ParsedReport parsedReport = new ParsedReport();
			RFTestReport testReport = RFUtils.parseReport(report);
			// List<RobotTestSuite> testSuiteList =
			// robotTestSuiteDao.findByDevopsProject(projectOptional.get());
			Map<String, RobotTestSuite> testSuiteMap = robotTestSuiteDao.findByDevopsProject(projectOptional.get())
					.stream().collect(Collectors.toMap(RobotTestSuite::getFilePath, Function.identity()));

			RFTestSuite baseSuite = testReport.getSuite();
			List<RFTestSuite> executedTestSuites = baseSuite.getSuite();
			for (RFTestSuite executedTestSuite : executedTestSuites) {
				RobotTestSuite testSuite = testSuiteMap.get(executedTestSuite.getSource());
				Map<String, RobotTestCase> testCaseMap = null;
				if (null != testSuite) {
					testCaseMap = testSuite.getTestCaseList().stream()
							.collect(Collectors.toMap(RobotTestCase::getName, Function.identity()));
				} else {
					testCaseMap = new HashMap<>();
				}
				Log.info(">>>>> testCaseMap.size: " + testCaseMap.size());
				for (RFTestCase executedTestCase : executedTestSuite.getTest()) {
					parsedReport.totalCnt += 1;
					if ("PASS".equals(executedTestCase.getStatus().getStatus())) {
						parsedReport.passCnt += 1;
					} else {
						String bugLevel = dftBugLevel;
						if (testCaseMap.containsKey(executedTestCase.getName())) {
							RobotTestCase testCase = testCaseMap.get(executedTestCase.getName());
							Log.info(">>>>testCase: " + testCase);
							bugLevel = testCase.getBugLevel();
						} else {
							Log.info(">>>>NO testCase!");
						}

						// TODO 提交缺陷

						switch (bugLevel) {
						case BUG_LEVEL_FATAL:
							parsedReport.fatalCnt += 1;
							break;
						case BUG_LEVEL_CRITICAL:
							parsedReport.criticalCnt += 1;
							break;
						case BUG_LEVEL_MAJOR:
							parsedReport.majorCnt += 1;
							break;
						case BUG_LEVEL_MINOR:
							parsedReport.minorCnt += 1;
							break;
						}
					}
				}
			}
			Log.info(">>>>>parsedReport: " + parsedReport);
			result.setSuccess(true);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_PARSE_TEST_SUITES));
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
			long groupId = projectOptional.get().getDevopsGroup().getId();
			int docsProjectId = projectOptional.get().getDocsProjectId();

			GitlabClient gitlabClient = GitlabClientBuilder.getOrCreateByGroup(groupId);
			String token = GitlabClientBuilder.getTokenByGroup(groupId);

			ByteArrayOutputStream baos = downloadFromGitlab(gitlabClient, token, docsProjectId, path);
			Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));

			result.setSuccess(true);
			result.setData(resource);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_DOWNLOAD_FILE));
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
			long groupId = projectOptional.get().getDevopsGroup().getId();
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
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_UPLOAD_FILE));
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
			long groupId = projectOptional.get().getDevopsGroup().getId();
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

	public List<GitlabFile> listGitlabFiles(GitlabClient gitlabClient, String token, int gitlabProjectId,
			String subpath, boolean recursive) throws JsonParseException, JsonMappingException, IOException {
		List<GitlabFile> files = new ArrayList<>();
		Response response = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			if (recursive) {
				params.put(GitlabClient.PARAM_RECURSIVE, "true");
			}
			if (StringUtils.isNotBlank(subpath)) {
				params.put(GitlabClient.PARAM_PATH, subpath.trim());
			}

			while (true) {
				response = gitlabClient.listFiles(token, gitlabProjectId, params);
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
		} finally {
			IOUtils.closeQuietly(response);
		}
		return files;
	}

	private ByteArrayOutputStream downloadFromGitlab(GitlabClient gitlabClient, String token, int gitlabProjectId,
			String path) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
		Response response = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(GitlabClient.PARAM_REF, "master");
			String encodedPath = URLEncoder.encode(path, "UTF-8");
			Log.info(">>>>> encodedPath: " + encodedPath);
			// response = gitlabClient.downloadFile(token, docsProjectId,
			// URLEncoder.encode(path,
			// "UTF-8"), params);
			response = gitlabClient.downloadFile(token, gitlabProjectId, encodedPath, params);
			InputStream respIs = response.body().asInputStream();
			byte[] temp = new byte[1024];
			int readCnt = -1;
			while (-1 != (readCnt = respIs.read(temp))) {
				baos.write(temp, 0, readCnt);
			}
		} finally {
			IOUtils.closeQuietly(response);
		}
		return baos;
	}

	private static final String HEADER_X_NEXT_PAGE = "X-Next-Page";
	private static final Collection<String> empryStringCollection = new ArrayList<>(0);

	@Data
	static class ParsedReport {
		private int totalCnt = 0;
		private int passCnt = 0;
		private int fatalCnt = 0;
		private int criticalCnt = 0;
		private int majorCnt = 0;
		private int minorCnt = 0;
	}

	private static final String BUG_LEVEL_FATAL = "Fatal";
	private static final String BUG_LEVEL_CRITICAL = "Critical";
	private static final String BUG_LEVEL_MAJOR = "Major";
	private static final String BUG_LEVEL_MINOR = "Minor";

	private static final Set<String> validBugLevels = new HashSet<>(
			Lists.newArrayList(BUG_LEVEL_FATAL, BUG_LEVEL_CRITICAL, BUG_LEVEL_MAJOR, BUG_LEVEL_MINOR));

	private static final String dftBugLevel = BUG_LEVEL_CRITICAL;

}
