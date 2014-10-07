package com.fancypants.device.container.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.device.container.DeviceContainer;

@Component
@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "request")
public class DeviceContainerImpl implements DeviceContainer {

	private DeviceEntity deviceEntity;

	@Override
	public DeviceEntity getDeviceEntity() {
		return deviceEntity;
	}

	@Override
	public void setDeviceEntity(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
	}

}
