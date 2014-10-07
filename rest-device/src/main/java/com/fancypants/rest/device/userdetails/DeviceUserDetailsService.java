package com.fancypants.rest.device.userdetails;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.device.service.DeviceService;
import com.fancypants.rest.mapping.DeviceMapper;

@Service
public class DeviceUserDetailsService implements UserDetailsService {

	private static final String ADMIN_USERNAME = "devices.fancypants.com";

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private DeviceMapper mapper;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// create the list of authorities
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		// if this is the special admin user grant additional authority
		if (true == username.equals(ADMIN_USERNAME)) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			try {
				// lookup the device to be sure it exists
				deviceService.getDevice(username);
			} catch (AbstractServiceException e) {
				throw new UsernameNotFoundException("device=" + username
						+ " not found in database");
			}
			// provide the user authority
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		// create and return the user
		UserDetails user = new User(username, "", authorities);
		return user;
	}

}
