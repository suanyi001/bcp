package com.nokia.bcp.auth.service;

import java.util.List;

import com.nokia.bcp.auth.entity.DbUser;
import com.nokia.bcp.auth.entity.ServiceResult;

public interface UserService {

	ServiceResult<DbUser> login(String account, String password);

	ServiceResult<DbUser> addUser(DbUser user);

	ServiceResult<List<DbUser>> queryUsers();

	ServiceResult<DbUser> queryUser(long id);

	ServiceResult<DbUser> modifyUser(long id, DbUser user);

	ServiceResult<DbUser> deleteUser(long id);

	String KEY_MSG_ERR_LOGIN = "MSG_ERR_LOGIN";
	String KEY_MSG_LOGIN = "MSG_LOGIN";
	String KEY_MSG_ERR_ADD_USER = "MSG_ERR_ADD_USER";
	String KEY_MSG_ERR_QUE_USER = "MSG_ERR_QUE_USER";
	String KEY_MSG_ERR_MOD_USER = "MSG_ERR_MOD_USER";
	String KEY_MSG_ERR_DEL_USER = "MSG_ERR_DEL_USER";

}
