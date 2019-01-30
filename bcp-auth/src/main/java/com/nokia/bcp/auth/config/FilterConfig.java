package com.nokia.bcp.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

@Configuration
public class FilterConfig {

	@Bean
	public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
		return new OpenEntityManagerInViewFilter();
	}

}
