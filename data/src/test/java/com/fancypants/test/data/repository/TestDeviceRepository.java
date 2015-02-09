package com.fancypants.test.data.repository;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDeviceRepository extends
		AbstractTestRepository<String, DeviceEntity> implements
		DeviceRepository {

	public TestDeviceRepository(ObjectMapper mapper) {
		super(mapper, DeviceEntity.class);
	}

}
