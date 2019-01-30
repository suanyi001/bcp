package com.nokia.bcp.auth.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.bcp.auth.entity.GitlabFile;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.service.GitlabFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(consumes = "application/json", produces = "application/json", value = "文档接口")
public class FileController extends BaseController {
	private static final Logger Log = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private GitlabFileService fileService;

	@Value("${bcp.upload.tempPath:}")
	private String tempPath;

	@GetMapping(path = "/project/{id}/tree", produces = "application/json")
	@ApiOperation("查询文件列表")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public List<GitlabFile> queryFiles(@PathVariable(name = "id", required = true) int projectId,
			@RequestParam(name = "path", required = false) String subpath) {
		Log.info(">>>>> path: " + subpath);
		ServiceResult<List<GitlabFile>> result = fileService.listFiles(projectId, subpath);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@PostMapping(path = "/project/{id}/cases")
	@ApiOperation("解析测试用例文件")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public void parseTestSuites(@PathVariable(name = "id", required = true) int projectId,
			@RequestParam(name = "path", required = true) String subpath) {
		Log.info(">>>>> path: " + subpath);
		ServiceResult<Void> result = fileService.parseRFFiles(projectId, subpath);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

	@PutMapping(path = "/suite/{id}")
	@ApiOperation("修改测试套缺陷严重级别")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public void changeSuiteBugLevel(@PathVariable(name = "id", required = true) int testSuiteId,
			@RequestParam(name = "bugLevel", required = true) String bugLevel,
			@RequestParam(name = "recursive", required = false, defaultValue = "false") boolean recursive) {
		Log.info(">>>>> bugLevel: " + bugLevel);
		ServiceResult<Void> result = fileService.changeSuiteBugLevel(testSuiteId, bugLevel, recursive);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

	@PutMapping(path = "/case/{id}")
	@ApiOperation("修改测试用例缺陷严重级别")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public void changeCaseeBugLevel(@PathVariable(name = "id", required = true) int testCaseeId,
			@RequestParam(name = "bugLevel", required = true) String bugLevel) {
		Log.info(">>>>> bugLevel: " + bugLevel);
		ServiceResult<Void> result = fileService.changeCaseBugLevel(testCaseeId, bugLevel);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

	@PostMapping(path = "/project/{id}/report")
	@ApiOperation("解析测试报告")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public void parseTestReport(@PathVariable(name = "id", required = true) int projectId, @RequestBody String report) {
		Log.info(">>>>> report: " + report);
		ServiceResult<Void> result = fileService.parseRFReport(projectId, report);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

	@GetMapping(path = "/project/{id}/files/{path}")
	@ApiOperation("下载文件")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public ResponseEntity<Resource> downloadFile(@PathVariable(name = "id", required = true) int projectId,
			@PathVariable(name = "path", required = true) String path) {
		Log.info(">>>>> path: " + path);
		ServiceResult<Resource> result = fileService.downloadFile(projectId, path);
		if (result.isSuccess()) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("charset", "utf-8");
			// 设置下载文件名
			try {
				String filename = URLEncoder.encode(new File(path).getName(), "UTF-8");
				headers.add("Content-Disposition", "attachment;filename=\"" + filename + "\"");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ResponseEntity.ok().headers(headers)
					.contentType(MediaType.parseMediaType("application/x-msdownload")).body(result.getData());
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@PostMapping(path = "/project/{id}/files/{path}")
	@ApiOperation("上传文件")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public String uploadFile(@PathVariable(name = "id", required = true) int projectId,
			@PathVariable(name = "path", required = true) String path, @RequestParam("file") MultipartFile file) {
		Log.info(">>>>> path: " + path);
		ServiceResult<String> result = fileService.uploadFile(projectId, path, file);

		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@DeleteMapping(path = "/project/{id}/files/{path}")
	@ApiOperation("删除文件")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public String deleteFile(@PathVariable(name = "id", required = true) int projectId,
			@PathVariable(name = "path", required = true) String path, @RequestParam("file") MultipartFile file) {
		Log.info(">>>>> path: " + path);
		ServiceResult<String> result = fileService.deleteFile(projectId, path);

		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	// 把指定URL后的字符串全部截断当成参数
	// 这么做是为了防止URL中包含中文或者特殊字符（/等）时，匹配不了的问题
	// private static String extractPathFromPattern(final HttpServletRequest
	// request) {
	// String path = (String)
	// request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	// String bestMatchPattern = (String)
	// request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	// return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
	// }

}
