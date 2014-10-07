package com.fancypants.device.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.dynamodb.repository.HourlyRecordRepository;
import com.fancypants.device.container.DeviceContainer;

@Service
public class UsageService {

	@Autowired
	private DeviceContainer deviceContainer;

	@Autowired
	private HourlyRecordRepository hourlyRepository;

	public List<PowerConsumptionRecordEntity> getHourlyRecords() {
		// query for all hourly records
		List<PowerConsumptionRecordEntity> entities = hourlyRepository
				.findByDevice(deviceContainer.getDeviceEntity().getDevice());
		// return the set
		return entities;
	}
}
