package com.nokia.bcp.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.bcp.auth.entity.DbUser;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.service.UserService;
import com.nokia.bcp.auth.utils.LocaleUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(consumes = "application/json", produces = "application/json", value = "用户接口")
public class UserController extends BaseController {

	@Autowired
	private UserService roleService;

	@GetMapping(path = "/users/certificate")
	@ApiOperation("认证用户")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public String login(@RequestParam(name = "account", required = true) String account,
			@RequestParam(name = "password", required = true) String password) {
		ServiceResult<DbUser> result = roleService.login(account, password);
		if (result.isSuccess()) {
			String token = UUID.randomUUID().toString();
			cacheUser(token, result.getData());
			return HEADER_PREFIX + token;
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@PostMapping(path = "/users", consumes = "application/json", produces = "application/json")
	@ApiOperation("新建用户")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public DbUser addUser(@RequestBody DbUser user) {
		DbUser operator = getOperator();
		if (null == operator) {
			setRespStatus(401, LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NOT_AUTH));
			return null;
		}

		Long operatorId = operator.getId();
		user.setCreatedBy(operatorId);
		user.setLastModifiedBy(operatorId);

		ServiceResult<DbUser> result = roleService.addUser(user);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@GetMapping(path = "/users", produces = "application/json")
	@ApiOperation("查询所有用户信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public List<DbUser> queryUsers() {
		ServiceResult<List<DbUser>> result = roleService.queryUsers();
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@GetMapping(path = "/users/{id}", produces = "application/json")
	@ApiOperation("查询用户信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public DbUser queryUser(@PathVariable(name = "id", required = true) long id) {
		ServiceResult<DbUser> result = roleService.queryUser(id);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@PutMapping(path = "/users/{id}", consumes = "application/json", produces = "application/json")
	@ApiOperation("更新用户信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public DbUser modifyUser(@PathVariable(name = "id", required = true) long id, @RequestBody DbUser user) {
		DbUser operator = getOperator();
		if (null == operator) {
			setRespStatus(401, LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NOT_AUTH));
			return null;
		}

		user.setLastModifiedBy(operator.getId());

		ServiceResult<DbUser> result = roleService.modifyUser(id, user);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@DeleteMapping(path = "/users/{id}")
	@ApiOperation("删除用户")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public void modifyUser(@PathVariable(name = "id", required = true) long id) {
		ServiceResult<DbUser> result = roleService.deleteUser(id);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

}
