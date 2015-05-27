package com.fancypants.rest.device.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.fancypants.rest.device.userdetails.AdminUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class RESTDeviceWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Configuration
	@Order(1)
	public static class AdminWebSecurityConfig extends
			WebSecurityConfigurerAdapter {
		private @Autowired
		AdminUserDetailsService userDetailsService;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/admin/**").authenticated()
					.and().userDetailsService(userDetailsService).httpBasic()
					.and().csrf().disable().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}
	}

	@Configuration
	@Order(2)
	public static class QueryWebSecurityConfig extends
			WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/query/**").anonymous().and()
					.csrf().disable().sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}
	}

	@Configuration
	@Order(3)
	public static class RejectWebSecurityConfig extends
			WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().denyAll();
		}
	}
}
