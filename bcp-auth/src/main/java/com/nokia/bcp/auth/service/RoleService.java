package com.nokia.bcp.auth.service;

import java.util.List;

import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.entity.SysRole;

public interface RoleService {

	ServiceResult<SysRole> addRole(SysRole role);

	ServiceResult<List<SysRole>> queryRoles();

	ServiceResult<SysRole> queryRole(long id);

	ServiceResult<SysRole> modifyRole(long id, SysRole role);

	ServiceResult<SysRole> deleteRole(long id);

	String KEY_MSG_ERR_ADD_ROLE = "MSG_ERR_ADD_ROLE";
	String KEY_MSG_ERR_QUE_ROLE = "MSG_ERR_QUE_ROLE";
	String KEY_MSG_ERR_MOD_ROLE = "MSG_ERR_MOD_ROLE";
	String KEY_MSG_ERR_DEL_ROLE = "MSG_ERR_DEL_ROLE";

}
