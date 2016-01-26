package com.fancypants.device.service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.data.entity.DeviceEntity;

public interface DeviceService {

	DeviceEntity getDevice(String deviceId) throws AbstractServiceException;
	
	DeviceEntity findDevice(String deviceId) throws AbstractServiceException;

	void createDevice(DeviceEntity deviceEntity) throws AbstractServiceException;

	void updateDevice(String deviceId, DeviceEntity deviceEntity) throws AbstractServiceException;

	void deleteDevice(String deviceId) throws AbstractServiceException;
}
