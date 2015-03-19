package com.fancypants.device.userdetails;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.field.address.AddressBuilder;
import org.apache.james.mime4j.field.address.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DeviceUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory
			.getLogger(DeviceUserDetailsService.class);

	private static final String ADMIN_USERNAME = "admin";

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private DeviceContainer deviceContainer;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		LOG.trace("loadUserByUsername entry", username);
		// create the list of authorities
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(
				1);
		try {
			// parse the email address from the certificate
			Mailbox address = (Mailbox) AddressBuilder.DEFAULT
					.parseAddress(username);
			// if this is the special admin user grant additional authority
			if (true == address.getLocalPart().equalsIgnoreCase(ADMIN_USERNAME)) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			} else {
				// lookup the device to be sure it exists
				DeviceEntity deviceEntity = deviceService.getDevice(address
						.getLocalPart());

				// cache the device
				deviceContainer.setDeviceEntity(deviceEntity);

				// provide the user authority
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			}
			// create and return the user
			UserDetails user = new User(address.getLocalPart(), "", authorities);
			LOG.trace("loadUserByUsername exit", user);
			return user;
		} catch (AbstractServiceException e) {
			LOG.warn("User="
					+ username
					+ " presented a valid certificate but was not found in the database");
			throw new UsernameNotFoundException("device=" + username
					+ " not found in database");
		} catch (ParseException e) {
			LOG.error("error parsing username=" + username, e);
			throw new UsernameNotFoundException("device=" + username
					+ " is not a valid RFC2822 address");
		}
	}
}
