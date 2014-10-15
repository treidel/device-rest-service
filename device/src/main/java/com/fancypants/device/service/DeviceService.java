package com.fancypants.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.device.container.DeviceContainer;

@Service
public class DeviceService {

	private static final int MAX_CIRCUITS = 64;

	@Autowired
	private DeviceRepository repository;

	@Autowired
	private DeviceContainer deviceContainer;

	public DeviceEntity getDevice(String deviceId)
			throws AbstractServiceException {
		// find the device
		DeviceEntity deviceEntity = repository.findOne(deviceId);
		if (null == deviceEntity) {
			throw new BusinessLogicException("device not found");
		}
		// cache the device object
		deviceContainer.setDeviceEntity(deviceEntity);
		// done
		return deviceEntity;
	}

	public void createDevice(DeviceEntity deviceEntity)
			throws AbstractServiceException {
		// validate that the number of reported circuits does not exceed the
		// maximum
		if (deviceEntity.getCircuits().size() > MAX_CIRCUITS) {
			throw new DataValidationException("number of circuits ("
					+ deviceEntity.getCircuits().size()
					+ ") exceeds the maximum (" + MAX_CIRCUITS + ")");
		}
		// ensure this device id does not already exist
		if (null != repository.findOne(deviceEntity.getDevice())) {
			throw new BusinessLogicException("Device already exists");
		}

		// TBD: possibly generate and/or validate the serial number format

		// write the device to the database
		repository.save(deviceEntity);

	}

	public void updateDevice(DeviceEntity deviceEntity)
			throws AbstractServiceException {
		// TBD: validate that the reported circuits has not changed

		// ensure they are not trying to change the device id
		if (false == deviceEntity.getDevice().equals(
				deviceContainer.getDeviceEntity().getDevice())) {
			throw new DataValidationException(
					"can not change device identifier");
		}

		// write the device to the database
		repository.save(deviceEntity);

	}

}
