package com.fancypants.test.data.device.dynamodb;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;
import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DynamoDBConfig.class)
public class DeviceTests extends AbstractTest {

	private static final DeviceEntity DEVICE1;  
	private static final String INVALID_DEVICE_NAME = "XYZ789";

	private @Autowired
	DeviceRepository repository;

	static {
		// setup the test records
		Set<CircuitEntity> circuits = new TreeSet<CircuitEntity>();
		for (int i = 1; i <= 16; i++) {
			CircuitEntity circuit = new CircuitEntity(i, String.valueOf(i), 120.0f, 30.0f);
			circuits.add(circuit);
		}
		DEVICE1 = new DeviceEntity("ABCD1234", "00000001", circuits, new Date());		
	}

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
		repository.save(DEVICE1);
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
		DeviceEntity device = repository.findOne(DEVICE1.getDevice());
		Assert.isTrue(null != device);
	}

	@Test
	public void queryInvalidTest() {
		DeviceEntity device = repository.findOne(INVALID_DEVICE_NAME);
		Assert.isNull(device);
	}

}
