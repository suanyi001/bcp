package com.nokia.bcp.auth.service;

import java.util.List;

import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.entity.SysAuthority;

public interface AuthorityService {

	ServiceResult<SysAuthority> addAuthority(SysAuthority authority);

	ServiceResult<List<SysAuthority>> queryAuthorities();

	ServiceResult<SysAuthority> queryAuthority(long id);

	ServiceResult<SysAuthority> modifyAuthority(long id, SysAuthority authority);

	ServiceResult<SysAuthority> deleteAuthority(long id);

	String KEY_MSG_ERR_ADD_AUTHORITY = "MSG_ERR_ADD_AUTHORITY";
	String KEY_MSG_ERR_QUE_AUTHORITY = "MSG_ERR_QUE_AUTHORITY";
	String KEY_MSG_ERR_MOD_AUTHORITY = "MSG_ERR_MOD_AUTHORITY";
	String KEY_MSG_ERR_DEL_AUTHORITY = "MSG_ERR_DEL_AUTHORITY";

}
