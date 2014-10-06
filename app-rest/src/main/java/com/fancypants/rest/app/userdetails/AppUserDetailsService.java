package com.fancypants.rest.app.userdetails;

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

import com.fancypants.rest.domain.Device;
import com.fancypants.rest.exception.AbstractServiceException;
import com.fancypants.rest.service.DeviceService;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private DeviceService deviceService;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// create the list of authorities
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		try {
			// lookup the device to be sure it exists
			Device device = deviceService.getDevice(username);
			// provide the user authority
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			// create and return the user
			UserDetails user = new User(username, device.getSerialNumber(),
					authorities);
			return user;
		} catch (AbstractServiceException e) {
			throw new UsernameNotFoundException("device=" + username
					+ " not found in database");
		}
	}

}
