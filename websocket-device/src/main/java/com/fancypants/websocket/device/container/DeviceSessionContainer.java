package com.fancypants.websocket.device.container;

import java.security.Principal;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.device.container.DeviceContainer;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DeviceSessionContainer implements DeviceContainer {

	private DeviceEntity deviceEntity = null;
	private Principal user = null;
	private boolean registered = false;

	@Override
	public DeviceEntity getDeviceEntity() {
		return deviceEntity;
	}

	@Override
	public void setDeviceEntity(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
	}

	public Principal getUser() {
		return user;
	}

	public void setUser(Principal user) {
		this.user = user;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

}
