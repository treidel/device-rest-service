package com.fancypants.rest.device.userdetails;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AdminUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory
			.getLogger(AdminUserDetailsService.class);

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		LOG.trace("loadUserByUsername enter {}={}", "username", username);
		// create the list of authorities
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		// create and return the user
		UserDetails user = new User(username, username, authorities);

		LOG.trace("loadUserByUsername exit {}", user);
		return user;
	}

}
