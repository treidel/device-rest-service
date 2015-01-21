package com.fancypants.rest.app.container;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.device.container.DeviceContainer;

@Component
@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "request")
public class RESTAppContainer implements DeviceContainer {

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