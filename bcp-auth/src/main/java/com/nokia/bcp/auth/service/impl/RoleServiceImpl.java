package com.nokia.bcp.auth.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.entity.SysRole;
import com.nokia.bcp.auth.repository.RoleRepository;
import com.nokia.bcp.auth.service.RoleService;
import com.nokia.bcp.auth.utils.LocaleUtils;

@Service
public class RoleServiceImpl implements RoleService {

	private static final Logger Log = LoggerFactory.getLogger(RoleServiceImpl.class);

	@Autowired
	private RoleRepository roleRepository;

	public ServiceResult<SysRole> addRole(SysRole role) {
		ServiceResult<SysRole> result = new ServiceResult<>();
		try {
			SysRole newRole = roleRepository.save(role);
			result.setSuccess(true);
			result.setData(newRole);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_ADD_ROLE));
		}
		return result;
	}

	public ServiceResult<List<SysRole>> queryRoles() {
		ServiceResult<List<SysRole>> result = new ServiceResult<>();
		try {
			List<SysRole> authorities = (List<SysRole>) roleRepository.findAll();
			result.setSuccess(true);
			result.setData(authorities);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_ROLE));
		}
		return result;
	}

	public ServiceResult<SysRole> queryRole(long id) {
		ServiceResult<SysRole> result = new ServiceResult<>();
		try {
			Optional<SysRole> authority = roleRepository.findById(id);
			if (authority.isPresent()) {
				result.setSuccess(true);
				result.setData(authority.get());
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_ROLE));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_ROLE));
		}
		return result;
	}

	public ServiceResult<SysRole> modifyRole(long id, SysRole role) {
		ServiceResult<SysRole> result = new ServiceResult<>();
		try {
			Optional<SysRole> oldAuthority = roleRepository.findById(id);
			if (oldAuthority.isPresent()) {
				SysRole newRole = oldAuthority.get();
				newRole.setRoleDesc(role.getRoleDesc());
				newRole.setLastModifiedDate(new Date());
				newRole = roleRepository.save(newRole);
				result.setSuccess(true);
				result.setData(newRole);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_MOD_ROLE));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_MOD_ROLE));
		}
		return result;
	}

	public ServiceResult<SysRole> deleteRole(long id) {
		ServiceResult<SysRole> result = new ServiceResult<>();
		try {
			roleRepository.deleteById(id);
			result.setSuccess(true);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_DEL_ROLE));
		}
		return result;
	}

}
