package com.fancypants.test.data.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.data.device.repository.RawRecordRepository;
import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.data.repository.TestDeviceRepository;
import com.fancypants.test.data.repository.TestHourlyRecordRepository;
import com.fancypants.test.data.repository.TestRawRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackageClasses = { TestDataScanMe.class })
public class DataTestConfig {

	@Autowired
	private ObjectMapper mapper;

	@Bean
	public DeviceRepository deviceRepository() {
		return new TestDeviceRepository(mapper);
	}

	@Bean
	public HourlyRecordRepository hourlyRecordRepository() {
		return new TestHourlyRecordRepository(mapper);
	}

	@Bean
	public RawRecordRepository rawRecordRepository() {
		return new TestRawRecordRepository(mapper);
	}

}
