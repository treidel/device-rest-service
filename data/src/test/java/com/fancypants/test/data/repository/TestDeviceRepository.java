package com.fancypants.test.data.repository;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDeviceRepository extends
		AbstractTestRepository<String, DeviceEntity> implements
		DeviceRepository {

	private static final long serialVersionUID = 1398700548747613221L;

	public TestDeviceRepository(ObjectMapper mapper) {
		super(mapper, DeviceEntity.class);
	}

}
