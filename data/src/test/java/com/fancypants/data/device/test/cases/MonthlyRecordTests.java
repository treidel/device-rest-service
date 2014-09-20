package com.fancypants.data.device.test.cases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.repository.MonthlyRecordRepository;
import com.fancypants.data.device.dynamodb.repository.impl.MonthlyRecordRepositoryImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		DynamoDBConfig.class, MonthlyRecordRepositoryImpl.class })
public class MonthlyRecordTests extends AbstractTest {

	private static final PowerConsumptionRecordEntity RECORD1 = new PowerConsumptionRecordEntity();
	private static final PowerConsumptionRecordEntity RECORD2 = new PowerConsumptionRecordEntity();
	private static final Collection<PowerConsumptionRecordEntity> RECORDS = new ArrayList<PowerConsumptionRecordEntity>(
			2);

	private @Autowired
	MonthlyRecordRepository repository;

	@BeforeClass
	public static void init() {
		// setup the test records
		RECORD1.setDevice("ABC1234");
		RECORD1.setDate(iso8601DateFormat.format(new Date()));
		for (int i = RawRecordEntity.MIN_CIRCUIT; i <= RawRecordEntity.MAX_CIRCUIT; i++) {
			RECORD1.setMeasurement(i, 10.0f);
		}

		RECORD2.setDevice("ABC1234");
		RECORD2.setDate(iso8601DateFormat.format(new Date()));
		for (int i = RawRecordEntity.MIN_CIRCUIT; i <= RawRecordEntity.MAX_CIRCUIT; i++) {
			RECORD2.setMeasurement(i, 20.0f);
		}

		// setup the list of records
		RECORDS.add(RECORD1);
		RECORDS.add(RECORD2);
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
		repository.insertOrIncrement(RECORD1);
	}

	@Test
	public void updateTest() {
		// pre-create
		createTest();
		// now update 
		repository.insertOrIncrement(RECORD1);
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		bulkInsertTest();
		// query for all records
		List<PowerConsumptionRecordEntity> records = repository.findByDevice(RECORD1.getDevice());
		Assert.isTrue(RECORDS.size() == records.size());
	}


	@Test
	public void bulkInsertTest() {
		for (PowerConsumptionRecordEntity record : RECORDS) {
			repository.insertOrIncrement(record);
		}
		Assert.isTrue(RECORDS.size() == repository.count());
	}

}
