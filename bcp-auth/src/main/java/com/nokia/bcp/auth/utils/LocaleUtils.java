package com.nokia.bcp.auth.utils;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocaleUtils {

	private static MessageSource messageSource;

	public static String getLocaleMsg(String key) {
		Locale locale = LocaleContextHolder.getLocale();
		System.out.println(locale.getDisplayCountry());
		return messageSource.getMessage(key, null, locale);
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) throws BeansException {
		if (LocaleUtils.messageSource == null) {
			LocaleUtils.messageSource = messageSource;
		}
	}

}
