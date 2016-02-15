package com.fancypants.websocket.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.fancypants.app.userdetails.AppUserDetailsService;
import com.fancypants.rest.authentication.CustomSecurityConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WSAppWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AppUserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// configure the bulk of our security settings
		// 1) allow access to /index.html so that the load balancer can see us
		// 2) all other requests must be authenticated
		// 3) enable sessions which are needed for websockets
		// 4) use our custom user lookup service
		// 5) enable HTTP basic authentication
		// 6) disable cross-domain request forwarding protection as this
		// prevents HTML5 apps from connecting
		http.authorizeRequests().antMatchers("/*.html").permitAll().and()
				.authorizeRequests().anyRequest().authenticated().and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
				.userDetailsService(userDetailsService).httpBasic().and()
				.csrf().disable();
		// add the configurer for the custom filter
		http.apply(new CustomSecurityConfigurer());
	}

	@Override
	protected UserDetailsService userDetailsService() {
		return userDetailsService;
	}

}
