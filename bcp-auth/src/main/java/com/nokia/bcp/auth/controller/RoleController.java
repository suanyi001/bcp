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
import com.nokia.bcp.auth.entity.SysRole;
import com.nokia.bcp.auth.service.RoleService;
import com.nokia.bcp.auth.utils.LocaleUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(consumes = "application/json", produces = "application/json", value = "角色接口")
public class RoleController extends BaseController {

	@Autowired
	private RoleService roleService;

	@PostMapping(path = "/roles", consumes = "application/json", produces = "application/json")
	@ApiOperation("新建角色")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public SysRole addRole(@RequestBody SysRole role) {
		DbUser operator = getOperator();
		if (null == operator) {
			setRespStatus(401, LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NOT_AUTH));
			return null;
		}

		Long operatorId = operator.getId();
		role.setCreatedBy(operatorId);
		role.setLastModifiedBy(operatorId);

		ServiceResult<SysRole> result = roleService.addRole(role);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@GetMapping(path = "/roles", produces = "application/json")
	@ApiOperation("查询所有角色信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public List<SysRole> queryRoles() {
		ServiceResult<List<SysRole>> result = roleService.queryRoles();
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@GetMapping(path = "/roles/{id}", produces = "application/json")
	@ApiOperation("查询角色信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public SysRole queryRole(@PathVariable(name = "id", required = true) long id) {
		ServiceResult<SysRole> result = roleService.queryRole(id);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@PutMapping(path = "/roles/{id}", consumes = "application/json", produces = "application/json")
	@ApiOperation("更新角色信息")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误"), @ApiResponse(code = 401, message = "鉴权失败") })
	public SysRole modifyRole(@PathVariable(name = "id", required = true) long id, @RequestBody SysRole role) {
		DbUser operator = getOperator();
		if (null == operator) {
			setRespStatus(401, LocaleUtils.getLocaleMsg(KEY_MSG_ERR_NOT_AUTH));
			return null;
		}

		role.setLastModifiedBy(operator.getId());

		ServiceResult<SysRole> result = roleService.modifyRole(id, role);
		if (result.isSuccess()) {
			return result.getData();
		} else {
			setRespStatus(501, result.getMessage());
			return null;
		}
	}

	@DeleteMapping(path = "/roles/{id}")
	@ApiOperation("删除角色")
	@ApiResponses({ @ApiResponse(code = 501, message = "参数错误") })
	public void modifyUser(@PathVariable(name = "id", required = true) long id) {
		ServiceResult<SysRole> result = roleService.deleteRole(id);
		if (!result.isSuccess()) {
			setRespStatus(501, result.getMessage());
		}
	}

}
