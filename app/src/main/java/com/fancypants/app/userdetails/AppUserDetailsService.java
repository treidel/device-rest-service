package com.fancypants.app.userdetails;

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
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.device.service.DeviceService;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceContainer deviceContainer;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// create the list of authorities
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		try {
			// lookup the device to be sure it exists
			DeviceEntity deviceEntity = deviceService.getDevice(username);
			// store the device 
			deviceContainer.setDeviceEntity(deviceEntity);
			// provide the user authority
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			// create and return the user
			UserDetails user = new User(username,
					deviceEntity.getSerialNumber(), authorities);
			return user;
		} catch (AbstractServiceException e) {
			throw new UsernameNotFoundException("device=" + username
					+ " not found in database");
		}
	}

}
