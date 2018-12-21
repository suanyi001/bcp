package com.nokia.bcp.auth.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.nokia.bcp.auth.entity.DbUser;

public class BaseController {

	private static final Logger Log = LoggerFactory.getLogger(BaseController.class);

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;

	@Autowired
	protected RedisTemplate<String, DbUser> redisTemplate;

	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}

	protected void setRespStatus(int code, String msg) {
		try {
			response.sendError(code, msg);
		} catch (IOException e) {
			Log.error("", e);
		}
	}

	protected DbUser getOperator() {
		DbUser operator = null;
		String token = request.getHeader(HEADER_KEY);
		if (StringUtils.isNotEmpty(token) && token.startsWith(HEADER_PREFIX)) {
			token = token.substring(TOKEN_INDEX).trim();
			operator = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
		}
		return operator;
	}

	protected void cacheUser(String token, DbUser user) {
		if (null != user) {
			redisTemplate.opsForValue().set(TOKEN_PREFIX + token, user, 3600L, TimeUnit.SECONDS);
		}
	}

	protected String HEADER_KEY = "Authorization";
	protected String HEADER_PREFIX = "Bearer ";
	protected int TOKEN_INDEX = HEADER_PREFIX.length();
	protected String TOKEN_PREFIX = "BCP_TOKEN_PREFIX_";
	protected String KEY_MSG_ERR_NOT_AUTH = "MSG_ERR_NOT_AUTH";
}
