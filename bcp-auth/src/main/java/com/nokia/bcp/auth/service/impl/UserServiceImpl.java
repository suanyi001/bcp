package com.nokia.bcp.auth.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nokia.bcp.auth.entity.DbUser;
import com.nokia.bcp.auth.entity.LdapUser;
import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.repository.LdapUserRepository;
import com.nokia.bcp.auth.repository.UserRepository;
import com.nokia.bcp.auth.service.UserService;
import com.nokia.bcp.auth.utils.HashedPwdUtils;
import com.nokia.bcp.auth.utils.LocaleUtils;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger Log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LdapUserRepository ldapUserRepository;

	public ServiceResult<DbUser> login(String account, String password) {
		ServiceResult<DbUser> result = new ServiceResult<>();
		try {
			boolean success = ldapUserRepository.authenticate(account, password);
			if (success) {
				DbUser user = userRepository.findByAccountOrEmail(account, account);
				result.setSuccess(true);
				result.setData(user);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_LOGIN));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_LOGIN));
		}
		return result;
	}

	public ServiceResult<DbUser> addUser(DbUser user) {
		ServiceResult<DbUser> result = new ServiceResult<>();
		try {
			String account = user.getAccount();
			LdapUser ldapUser = new LdapUser();
			ldapUser.setUid(account);
			ldapUser.setAccount(account);
			ldapUser.setEmail(user.getEmail());
			ldapUser.setUsername(user.getUsername());
			ldapUser.setPassword(HashedPwdUtils.createLdapPwd(user.getPassword()));
			ldapUserRepository.save(ldapUser);

			DbUser newUser = userRepository.save(user);
			result.setSuccess(true);
			result.setData(newUser);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_ADD_USER));
		}
		return result;
	}

	public ServiceResult<List<DbUser>> queryUsers() {
		ServiceResult<List<DbUser>> result = new ServiceResult<>();
		try {
			List<DbUser> users = (List<DbUser>) userRepository.findAll();
			result.setSuccess(true);
			result.setData(users);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_USER));
		}
		return result;
	}

	public ServiceResult<DbUser> queryUser(long id) {
		ServiceResult<DbUser> result = new ServiceResult<>();
		try {
			Optional<DbUser> authority = userRepository.findById(id);
			if (authority.isPresent()) {
				result.setSuccess(true);
				result.setData(authority.get());
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_USER));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_USER));
		}
		return result;
	}

	public ServiceResult<DbUser> modifyUser(long id, DbUser user) {
		ServiceResult<DbUser> result = new ServiceResult<>();
		try {

			Optional<DbUser> oldUser = userRepository.findById(id);
			if (oldUser.isPresent()) {
				String account = oldUser.get().getAccount();
				LdapUser ldapUser = ldapUserRepository.findByAccount(account);
				ldapUser.setUsername(user.getUsername());
				ldapUser.setPassword(HashedPwdUtils.createLdapPwd(user.getPassword()));
				ldapUserRepository.save(ldapUser);

				DbUser newUser = oldUser.get();
				newUser.setUsername(user.getUsername());
				newUser.setPassword(user.getPassword());
				newUser.setLastModifiedDate(new Date());
				newUser = userRepository.save(newUser);
				result.setSuccess(true);
				result.setData(newUser);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_MOD_USER));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_MOD_USER));
		}
		return result;
	}

	public ServiceResult<DbUser> deleteUser(long id) {
		ServiceResult<DbUser> result = new ServiceResult<>();
		try {
			Optional<DbUser> user = userRepository.findById(id);
			if (user.isPresent()) {
				LdapUser ldapUser = ldapUserRepository.findByAccount(user.get().getAccount());
				ldapUserRepository.deleteById(ldapUser.getDn());

				userRepository.deleteById(id);
				result.setSuccess(true);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_DEL_USER));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_DEL_USER));
		}
		return result;
	}

}
