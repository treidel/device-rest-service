package com.fancypants.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.exception.AbstractServiceException;
import com.fancypants.rest.exception.BusinessLogicException;
import com.fancypants.rest.exception.DataValidationException;
import com.fancypants.rest.mapping.DeviceMapper;
import com.fancypants.rest.mapping.DeviceEntityMapper;
import com.fancypants.rest.request.DeviceContainer;

@Service
public class DeviceService {

	@Autowired
	private DeviceRepository repository;

	@Autowired
	private DeviceMapper entityMapper;

	@Autowired
	private DeviceEntityMapper deviceMapper;

	@Autowired
	private DeviceContainer deviceContainer;

	public Device getDevice(String deviceId) {
		// find the device
		DeviceEntity deviceEntity = repository.findOne(deviceId);
		if (null == deviceEntity) {
			return null;
		}
		// map the device
		Device device = entityMapper.convert(deviceEntity);

		// cache the device objects
		deviceContainer.setDevice(device);
		deviceContainer.setDeviceEntity(deviceEntity);
		// done
		return device;
	}

	public void createDevice(Device device) throws AbstractServiceException {
		// validate that the number of reported circuits does not exceed the
		// maximum
		if (device.getCircuits().size() > RawRecordEntity.MAX_CIRCUIT) {
			throw new DataValidationException("number of circuits ("
					+ device.getCircuits().size() + ") exceeds the maximum ("
					+ RawRecordEntity.MAX_CIRCUIT + ")");
		}
		// ensure this device id does not already exist
		DeviceEntity deviceEntity = repository.findOne(device.getName());
		if (null != deviceEntity) {
			throw new BusinessLogicException("Device already exists");
		}

		// TBD: possibly generate and/or validate the serial number format

		// convert the device to a database entity
		deviceEntity = deviceMapper.convert(device);

		// write the device to the database
		repository.save(deviceEntity);

	}

	public void updateDevice(Device device) throws AbstractServiceException {
		// TBD: validate that the reported circuits has not changed

		// ensure they are not trying to change the device id
		if (false == device.getName().equals(
				deviceContainer.getDevice().getName())) {
			throw new DataValidationException(
					"can not change device identifier");
		}

		// convert the device to a database entity
		DeviceEntity deviceEntity = deviceMapper.convert(device);

		// write the device to the database
		repository.save(deviceEntity);

	}

}
