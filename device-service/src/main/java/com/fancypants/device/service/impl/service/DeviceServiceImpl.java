package com.fancypants.device.service.impl.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.repository.DeviceRepository;
import com.fancypants.device.service.DeviceService;
import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicManager;

@Service
@Lazy
public class DeviceServiceImpl implements DeviceService {

	private static final Logger LOG = LoggerFactory.getLogger(DeviceServiceImpl.class);

	private static final int MAX_CIRCUITS = 64;

	@Autowired
	private DeviceRepository repository;

	@Autowired(required = false)
	private TopicManager topicManager;

	@Override
	public DeviceEntity findDevice(String deviceId) throws AbstractServiceException {
		LOG.trace("findDevice enter {}={}", "deviceId", deviceId);
		// query for the device
		DeviceEntity deviceEntity = repository.findOne(deviceId);
		// done
		LOG.trace("findDevice exit {}", deviceEntity);
		return deviceEntity;
	}

	@Override
	public DeviceEntity getDevice(String deviceId) throws AbstractServiceException {
		LOG.trace("getDevice enter {}={}", "deviceId", deviceId);
		// query for the device
		DeviceEntity deviceEntity = repository.findOne(deviceId);
		if (null == deviceEntity) {
			throw new BusinessLogicException("device not found");
		}
		// done
		LOG.trace("getDevice exit {}", deviceEntity);
		return deviceEntity;
	}

	@Override
	public void createDevice(DeviceEntity deviceEntity) throws AbstractServiceException {
		LOG.trace("createDevice enter {}={}", "deviceEntity", deviceEntity);
		// validate that the number of reported circuits does not exceed the
		// maximum
		if (deviceEntity.getCircuits().size() > MAX_CIRCUITS) {
			throw new DataValidationException("number of circuits (" + deviceEntity.getCircuits().size()
					+ ") exceeds the maximum (" + MAX_CIRCUITS + ")");
		}
		// ensure this device id does not already exist
		if (null != repository.findOne(deviceEntity.getDevice())) {
			throw new BusinessLogicException("Device already exists");
		}

		// TBD: possibly generate and/or validate the serial number format

		// write the device to the database
		repository.save(deviceEntity);

		try {
			// create a topic for this device
			topicManager.topicCreate(deviceEntity.getDevice());
		} catch (AbstractMessageException e) {
			// log it and continue on
			LOG.error("error creating topic", e);
		}
		LOG.trace("createDevice exit");
	}

	@Override
	public void updateDevice(String deviceId, DeviceEntity deviceEntity) throws AbstractServiceException {
		LOG.trace("updateDevice enter {}={} {}={}", "deviceId", deviceId, "deviceEntity", deviceEntity);
		// find the current device entity
		DeviceEntity currentEntity = repository.findOne(deviceId);
		if (null == currentEntity) {
			LOG.warn("deviceId={} not found", deviceId);
			throw new BusinessLogicException("device not found");
		}
		// TBD: validate that the reported circuits has not changed

		// ensure they are not trying to change the device id
		if (false == deviceEntity.getDevice().equals(deviceId)) {
			throw new DataValidationException("can not change device identifier");
		}

		// set the last updated timestamp
		deviceEntity.setLastModifiedTimestamp(new Date());

		// write the device to the database
		repository.save(deviceEntity);

		LOG.trace("updateDevice exit");
	}

	@Override
	public void deleteDevice(String deviceId) throws AbstractServiceException {
		LOG.trace("deleteDevice enter {}={}", "deviceId", deviceId);
		// find the device entity
		DeviceEntity currentEntity = repository.findOne(deviceId);
		if (null == currentEntity) {
			LOG.warn("deviceId={} not found", deviceId);
			throw new BusinessLogicException("device not found");
		}
		// remove the device
		repository.delete(deviceId);
		LOG.trace("deleteDevice exit");
	}
}
