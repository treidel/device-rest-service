package com.fancypants.websocket.app.container;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.device.container.DeviceContainer;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebsocketSessionContainer implements DeviceContainer {

	private DeviceEntity deviceEntity = null;
	private boolean registered = false;

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	@Override
	public DeviceEntity getDeviceEntity() {
		return deviceEntity;
	}

	@Override
	public void setDeviceEntity(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
	}

}
