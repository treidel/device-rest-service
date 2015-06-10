package com.fancypants.rest.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class CustomSecurityConfigurer extends
		SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>
		implements SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {

	@Override
	public void configure(HttpSecurity builder) throws Exception {
		// create the filter
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
		// populate the authentication manager
		customAuthenticationFilter.setAuthenticationManager(builder
				.getSharedObject(AuthenticationManager.class));
		// add the filter before the HTTP basic filter
		builder.addFilterBefore(customAuthenticationFilter,
				BasicAuthenticationFilter.class);
	}

}
