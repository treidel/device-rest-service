package com.fancypants.device.container;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fancypants.data.entity.DeviceEntity;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class DeviceContainer {

	private DeviceEntity deviceEntity;

	public DeviceEntity getDeviceEntity() {
		return deviceEntity;
	}

	public void setDeviceEntity(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
	}
}