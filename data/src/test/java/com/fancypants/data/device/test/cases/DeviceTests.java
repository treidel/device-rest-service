package com.fancypants.data.device.test.cases;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;
import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DynamoDBConfig.class)
public class DeviceTests extends AbstractTest {

	private static final DeviceEntity DEVICE1 = new DeviceEntity();
	private static final String INVALID_DEVICE_NAME = "XYZ789";

	private @Autowired
	DeviceRepository repository;

	@BeforeClass
	public static void init() {
		// setup the test records
		DEVICE1.setDevice("ABC1234");
		DEVICE1.setSerialNumber("00000001");
		DEVICE1.setLastModifiedTimestamp(iso8601DateFormat.format(new Date()));
		Set<CircuitEntity> circuits = new TreeSet<CircuitEntity>();
		for (int i = RawRecordEntity.MIN_CIRCUIT; i <= RawRecordEntity.MAX_CIRCUIT; i++) {
			CircuitEntity circuit = new CircuitEntity(i, String.valueOf(i), 120.0f, 30.0f);
			circuits.add(circuit);
		}
		DEVICE1.setCircuits(circuits);
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
