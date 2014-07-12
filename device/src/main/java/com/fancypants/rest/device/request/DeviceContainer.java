package com.fancypants.rest.device.request;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.rest.domain.Device;

public interface DeviceContainer {

	DeviceEntity getDeviceEntity();

	void setDeviceEntity(DeviceEntity deviceEntity);

	Device getDevice();

	void setDevice(Device device);

}
