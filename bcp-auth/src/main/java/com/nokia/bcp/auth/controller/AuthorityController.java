package com.nokia.bcp.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.bcp.auth.entity.DbUser;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.entity.SysAuthority;
import com.nokia.bcp.auth.service.AuthorityService;
import com.nokia.bcp.auth.utils.LocaleUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(consumes = "application/json", produces = "application/json", value = "权限接口")
public class AuthorityController extends BaseController {

	@Autowired
	private AuthorityService authorityService;

	@PostMapping(path = "/authorities", consumes = "application/json", produces = "application/json")
	@ApiOperation("新建权限")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public SysAuthority addAuthority(@RequestBody SysAuthority authority) {
		DbUser operator = getOperator();
		if (null == operator) {
			setRespStatus(401, LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NOT_AUTH));
			return null;
		}

		Long operatorId = operator.getId();
		authority.setCreatedBy(operatorId);
		authority.setLastModifiedBy(operatorId);

		ServiceResult<SysAuthority> result = authorityService.addAuthority(authority);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@GetMapping(path = "/authorities", produces = "application/json")
	@ApiOperation("查询所有权限信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public List<SysAuthority> queryAuthorities() {
		ServiceResult<List<SysAuthority>> result = authorityService.queryAuthorities();
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@GetMapping(path = "/authorities/{id}", produces = "application/json")
	@ApiOperation("查询权限信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public SysAuthority queryAuthority(@PathVariable(name = "id", required = true) long id) {
		ServiceResult<SysAuthority> result = authorityService.queryAuthority(id);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@PutMapping(path = "/authorities/{id}", consumes = "application/json", produces = "application/json")
	@ApiOperation("更新权限信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public SysAuthority modifyAuthority(@PathVariable(name = "id", required = true) long id,
			@RequestBody SysAuthority authority) {
		DbUser operator = getOperator();
		if (null == operator) {
			setRespStatus(401, LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NOT_AUTH));
			return null;
		}

		authority.setLastModifiedBy(operator.getId());

		ServiceResult<SysAuthority> result = authorityService.modifyAuthority(id, authority);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@DeleteMapping(path = "/authorities/{id}")
	@ApiOperation("删除权限")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public void modifyAuthority(@PathVariable(name = "id", required = true) long id) {
		ServiceResult<SysAuthority> result = authorityService.deleteAuthority(id);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

}
