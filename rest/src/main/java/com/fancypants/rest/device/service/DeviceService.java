package com.fancypants.rest.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.mapping.DeviceEntityToDeviceMapper;
import com.fancypants.rest.device.request.DeviceContainer;

@Service
public class DeviceService {

	@Autowired
	private DeviceRepository repository;

	@Autowired
	private DeviceEntityToDeviceMapper mapper;

	@Autowired
	private DeviceContainer deviceContainer;

	public Device getDevice(String deviceId) {
		// find the device
		DeviceEntity deviceEntity = repository.findOne(deviceId);
		if (null == deviceEntity) {
			return null;
		}
		// map the device
		Device device = mapper.convert(deviceEntity);

		// cache the device objects
		deviceContainer.setDevice(device);
		deviceContainer.setDeviceEntity(deviceEntity);
		// done
		return device;
	}

}
