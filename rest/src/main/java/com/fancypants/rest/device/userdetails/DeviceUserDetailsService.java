package com.fancypants.rest.device.userdetails;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeviceUserDetailsService implements UserDetailsService {

	@PostConstruct
	public void init() {
		// set ourselves as the default user details service
		
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// TBD: verify the device has not been disabled via some kind of
		// database lookup

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		authorities.add(new SimpleGrantedAuthority("USER"));
		UserDetails user = new User(username, "", authorities);
		return user;
	}

}
