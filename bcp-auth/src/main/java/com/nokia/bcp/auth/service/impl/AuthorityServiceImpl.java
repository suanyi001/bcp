package com.nokia.bcp.auth.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nokia.bcp.auth.entity.ServiceResult;
import com.nokia.bcp.auth.entity.SysAuthority;
import com.nokia.bcp.auth.repository.AuthorityRepository;
import com.nokia.bcp.auth.service.AuthorityService;
import com.nokia.bcp.auth.utils.LocaleUtils;

@Service
public class AuthorityServiceImpl implements AuthorityService {

	private static final Logger Log = LoggerFactory.getLogger(AuthorityServiceImpl.class);

	@Autowired
	private AuthorityRepository authorityRepository;

	public ServiceResult<SysAuthority> addAuthority(SysAuthority authority) {
		ServiceResult<SysAuthority> result = new ServiceResult<>();
		try {
			SysAuthority newAuthority = authorityRepository.save(authority);
			result.setSuccess(true);
			result.setData(newAuthority);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_ADD_AUTHORITY));
		}
		return result;
	}

	public ServiceResult<List<SysAuthority>> queryAuthorities() {
		ServiceResult<List<SysAuthority>> result = new ServiceResult<>();
		try {
			List<SysAuthority> authorities = (List<SysAuthority>) authorityRepository.findAll();
			result.setSuccess(true);
			result.setData(authorities);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_AUTHORITY));
		}
		return result;
	}

	public ServiceResult<SysAuthority> queryAuthority(long id) {
		ServiceResult<SysAuthority> result = new ServiceResult<>();
		try {
			Optional<SysAuthority> authority = authorityRepository.findById(id);
			if (authority.isPresent()) {
				result.setSuccess(true);
				result.setData(authority.get());
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_AUTHORITY));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_QUE_AUTHORITY));
		}
		return result;
	}

	public ServiceResult<SysAuthority> modifyAuthority(long id, SysAuthority authority) {
		ServiceResult<SysAuthority> result = new ServiceResult<>();
		try {
			Optional<SysAuthority> oldAuthority = authorityRepository.findById(id);
			if (oldAuthority.isPresent()) {
				SysAuthority newAuthority = oldAuthority.get();
				newAuthority.setAuthorityDesc(authority.getAuthorityDesc());
				newAuthority.setLastModifiedDate(new Date());
				newAuthority = authorityRepository.save(newAuthority);
				result.setSuccess(true);
				result.setData(newAuthority);
			} else {
				result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_MOD_AUTHORITY));
			}
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_MOD_AUTHORITY));
		}
		return result;
	}

	public ServiceResult<SysAuthority> deleteAuthority(long id) {
		ServiceResult<SysAuthority> result = new ServiceResult<>();
		try {
			authorityRepository.deleteById(id);
			result.setSuccess(true);
		} catch (Exception e) {
			Log.error("", e);
			result.setMessage(LocaleUtils.getLocaleMsg(KEY_MSG_ERR_DEL_AUTHORITY));
		}
		return result;
	}

}
