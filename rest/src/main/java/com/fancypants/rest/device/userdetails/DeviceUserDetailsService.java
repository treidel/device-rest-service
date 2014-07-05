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

import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.mapping.DeviceEntityToDeviceMapper;
import com.fancypants.rest.device.service.DeviceService;

@Service
public class DeviceUserDetailsService implements UserDetailsService {

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private DeviceEntityToDeviceMapper mapper;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// lookup the device to be sure it exists
		Device device = deviceService.getDevice(username);
		if (null == device) {
			throw new UsernameNotFoundException("device=" + username + " not found in database");
		}

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		authorities.add(new SimpleGrantedAuthority("USER"));
		UserDetails user = new User(username, "", authorities);
		return user;
	}

}
