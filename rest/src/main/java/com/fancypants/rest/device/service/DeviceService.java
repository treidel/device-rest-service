package com.fancypants.rest.device.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.rest.device.domain.Device;

@Service
public class DeviceService {

	private @Autowired
	DeviceRepository repository;

	public Device getDevice(String deviceId) {
		return new Device("ABC!23", "12345678", new Date(), null);
	}

}
