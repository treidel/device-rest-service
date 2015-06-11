package com.fancypants.test.data.config;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.repository.DeviceRepository;
import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.data.values.DeviceValues;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, TestDataScanMe.class })
public class TestDataConfig {

	@Autowired
	private DeviceRepository deviceRepository;

	@PostConstruct
	@ConditionalOnMissingClass(Test.class)
	private void init() {
		// inject default device
		deviceRepository.save(DeviceValues.DEVICEENTITY);
	}
}
