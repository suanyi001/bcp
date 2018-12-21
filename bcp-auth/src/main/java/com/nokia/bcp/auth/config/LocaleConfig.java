package com.nokia.bcp.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * 多语言支持: </br>
 * 首先检查session中的SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME的值，如果有，采用此值</br>
 * 然后检查SessionLocaleResolver的defaultLocale属性，如果有，采用此值</br>
 * 返回HttpServletRequest的getLocale()，也就是HTTP请求中的Accept-Language的值</br>
 * 
 * @author wangyueda
 *
 */
@Configuration
public class LocaleConfig {

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		// slr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
		return slr;
	}

}
