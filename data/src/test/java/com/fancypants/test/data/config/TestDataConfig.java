package com.fancypants.test.data.config;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.repository.DeviceRepository;
import com.fancypants.data.repository.HourlyRecordRepository;
import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.data.values.DeviceValues;
import com.fancypants.test.data.values.HourlyRecordValues;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, TestDataScanMe.class })
public class TestDataConfig {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private HourlyRecordRepository hourlyRecordRepository;

	@PostConstruct
	@ConditionalOnMissingClass(Test.class)
	private void init() {
		// inject default data
		deviceRepository.save(DeviceValues.DEVICEENTITY);
		for (EnergyConsumptionRecordEntity record : HourlyRecordValues.RECORDS) {
			hourlyRecordRepository.save(record);
		}
	}
}
