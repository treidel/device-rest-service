package com.fancypants.device.container;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;

public interface DeviceContainer {

	DeviceEntity getDeviceEntity();

	void setDeviceEntity(DeviceEntity deviceEntity);
}
