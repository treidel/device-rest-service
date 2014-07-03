package com.fancypants.rest.device.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.fancypants.rest.device.domain.Device;

@Service
public class DeviceService {

	public Device getDevice(String deviceId) {
		return new Device("ABC!23", "12345678", new Date(), null);
	}

}
