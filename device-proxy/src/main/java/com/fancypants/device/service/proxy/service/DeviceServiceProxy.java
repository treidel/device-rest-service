package com.fancypants.device.service.proxy.service;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataPersistenceException;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.device.service.DeviceService;

@Service
public class DeviceServiceProxy implements DeviceService {

	private static final Logger LOG = LoggerFactory.getLogger(DeviceServiceProxy.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Value("${DEVICE_SERVICE_URL}")
	private URI deviceServiceURL;

	private URI devicesURL;

	@PostConstruct
	private void init() {
		LOG.trace("init enter");
		devicesURL = URI.create(deviceServiceURL.toString().replaceFirst("/*$", "") + "/devices");
		LOG.trace("init exit");
	}

	@Override
	public DeviceEntity findDevice(String deviceId) throws AbstractServiceException {
		LOG.trace("findDevice enter {}={}", "deviceId", deviceId);
		try {
			ResponseEntity<DeviceEntity> response = restTemplate.getForEntity(devicesURL, DeviceEntity.class);
			DeviceEntity deviceEntity = null;
			if (true == HttpStatus.OK.equals(response.getStatusCode())) {
				deviceEntity = response.getBody();
			}
			LOG.trace("findDevice exit {}", deviceEntity);
			return deviceEntity;
		} catch (HttpClientErrorException e) {
			throw new DataPersistenceException("error retrieving device info");
		}
	}

	@Override
	public DeviceEntity getDevice(String deviceId) throws AbstractServiceException {
		LOG.trace("getDevice enter {}={}", "deviceId", deviceId);
		URI deviceURL = URI.create(devicesURL.toString() + "/" + deviceId);
		try {
			ResponseEntity<DeviceEntity> response = restTemplate.getForEntity(deviceURL, DeviceEntity.class);
			if (true != HttpStatus.OK.equals(response.getStatusCode())) {
				throw new BusinessLogicException("device not found");
			}
			DeviceEntity deviceEntity = response.getBody();
			LOG.trace("getDevice exit {}", deviceEntity);
			return deviceEntity;
		} catch (HttpClientErrorException e) {
			throw new DataPersistenceException("error retrieving device info");
		}
	}

	@Override
	public void createDevice(DeviceEntity deviceEntity) throws AbstractServiceException {
		LOG.trace("createDevice enter {}={}", "deviceEntity", deviceEntity);
		try {
			restTemplate.postForLocation(devicesURL, deviceEntity);
		} catch (HttpClientErrorException e) {
			throw new DataPersistenceException("error saving device info");
		}
		LOG.trace("createDevice exit");
	}

	@Override
	public void updateDevice(String deviceId, DeviceEntity deviceEntity) throws AbstractServiceException {
		LOG.trace("updateDevice enter {}={} {}={}", "deviceId", deviceId, "deviceEntity", deviceEntity);
		// create the URL for the device
		URI deviceURL = URI.create(devicesURL.toString() + "/" + deviceId);
		try {
			restTemplate.postForLocation(deviceURL, deviceEntity);
		} catch (HttpClientErrorException e) {
			throw new DataPersistenceException("error updating device info");
		}
		LOG.trace("updateDevice exit");
	}

	@Override
	public void deleteDevice(String deviceId) throws AbstractServiceException {
		LOG.trace("deleteDevice enter {}={}", "deviceId", deviceId);
		// create the URL for the device
		URI deviceURL = URI.create(devicesURL.toString() + "/" + deviceId);
		try {
			restTemplate.delete(deviceURL);
		} catch (HttpClientErrorException e) {
			throw new DataPersistenceException("error removing device info");
		}
		LOG.trace("updateDevice exit");
	}

}
