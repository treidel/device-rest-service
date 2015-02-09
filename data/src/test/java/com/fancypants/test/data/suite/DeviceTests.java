package com.fancypants.test.data.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.test.data.config.DataTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DataTestConfig.class)
public class DeviceTests {

	private static final String INVALID_DEVICE_NAME = "XYZ789";

	private @Autowired
	DeviceRepository repository;

	@Before
	public void setup() {
		// pre-clean
		cleanup();
	}

	@After
	public void cleanup() {
		// remove all records just in case
		repository.deleteAll();
	}

	@Test
	public void createTest() {
		repository.save(DeviceEntity.TEST.DEVICEENTITY);
	}

	@Test
	public void duplicateCreateTest() {
		// pre-create
		createTest();
		// create + save again
		querySuccessTest();
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		createTest();
		// query for it
		DeviceEntity device = repository.findOne(DeviceEntity.TEST.DEVICEENTITY
				.getDevice());
		Assert.isTrue(null != device);
	}

	@Test
	public void queryInvalidTest() {
		DeviceEntity device = repository.findOne(INVALID_DEVICE_NAME);
		Assert.isNull(device);
	}

}
