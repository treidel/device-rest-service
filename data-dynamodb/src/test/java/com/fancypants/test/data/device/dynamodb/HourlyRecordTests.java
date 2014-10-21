package com.fancypants.test.data.device.dynamodb;

import java.util.List;

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
import com.fancypants.data.device.dynamodb.repository.DynamoDBHourlyRecordRepository;
import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		DynamoDBConfig.class, DynamoDBHourlyRecordRepository.class })
public class HourlyRecordTests extends AbstractTest {

	private @Autowired
	HourlyRecordRepository repository;

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
		repository.insertOrIncrement(PowerConsumptionRecordEntity.TEST.RECORD1);
	}

	@Test
	public void updateTest() {
		// pre-create
		createTest();
		// now update
		repository.insertOrIncrement(PowerConsumptionRecordEntity.TEST.RECORD1);
		// now query the row
		PowerConsumptionRecordEntity record = repository
				.findOne(PowerConsumptionRecordEntity.TEST.RECORD1.getId());
		// check the values - should be doubled
		float value1 = PowerConsumptionRecordEntity.TEST.RECORD1.getEnergy(1);
		float value2 = record.getEnergy(1);
		Assert.isTrue(value1 == value2 / 2);
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		bulkInsertTest();
		// query for all records
		List<PowerConsumptionRecordEntity> records = repository
				.findByDevice(PowerConsumptionRecordEntity.TEST.RECORD1
						.getDevice());
		Assert.isTrue(PowerConsumptionRecordEntity.TEST.RECORDS.length == records
				.size());
	}

	@Test
	public void bulkInsertTest() {
		for (PowerConsumptionRecordEntity record : PowerConsumptionRecordEntity.TEST.RECORDS) {
			repository.insertOrIncrement(record);
		}
		Assert.isTrue(PowerConsumptionRecordEntity.TEST.RECORDS.length == repository
				.count());
	}

}
