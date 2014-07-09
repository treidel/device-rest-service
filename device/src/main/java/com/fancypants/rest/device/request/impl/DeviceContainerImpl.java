package com.fancypants.rest.device.request.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.request.DeviceContainer;

@Component
@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "request")
public class DeviceContainerImpl implements DeviceContainer {

	private DeviceEntity deviceEntity;
	private Device device;

	@Override
	public DeviceEntity getDeviceEntity() {
		return deviceEntity;
	}

	@Override
	public void setDeviceEntity(DeviceEntity deviceEntity) {
		this.deviceEntity = deviceEntity;
	}

	@Override
	public Device getDevice() {
		return device;
	}

	@Override
	public void setDevice(Device device) {
		this.device = device;
	}
}
