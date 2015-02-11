package com.fancypants.test.data.repository;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.repository.DeviceRepository;

@Component
public class TestDeviceRepository extends
		AbstractTestRepository<DeviceEntity, String> implements
		DeviceRepository {

	private static final long serialVersionUID = 1398700548747613221L;

	public TestDeviceRepository() {
		super(DeviceEntity.class);
	}

}
