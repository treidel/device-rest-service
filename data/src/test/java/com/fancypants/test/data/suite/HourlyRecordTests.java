package com.fancypants.test.data.suite;

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

import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.test.data.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestConfig.class)
public class HourlyRecordTests {

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
		repository.insertOrIncrement(EnergyConsumptionRecordEntity.TEST.RECORD1);
	}

	@Test
	public void updateTest() {
		// pre-create
		createTest();
		// now update
		repository.insertOrIncrement(EnergyConsumptionRecordEntity.TEST.RECORD1);
		// now query the row
		EnergyConsumptionRecordEntity record = repository
				.findOne(EnergyConsumptionRecordEntity.TEST.RECORD1.getId());
		// check the values - should be doubled
		float value1 = EnergyConsumptionRecordEntity.TEST.RECORD1.getEnergy(1);
		float value2 = record.getEnergy(1);
		Assert.isTrue(value1 == value2 / 2);
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		bulkInsertTest();
		// query for all records
		List<EnergyConsumptionRecordEntity> records = repository
				.findByDevice(EnergyConsumptionRecordEntity.TEST.RECORD1
						.getDevice());
		Assert.isTrue(EnergyConsumptionRecordEntity.TEST.RECORDS.length == records
				.size());
	}

	@Test
	public void bulkInsertTest() {
		for (EnergyConsumptionRecordEntity record : EnergyConsumptionRecordEntity.TEST.RECORDS) {
			repository.insertOrIncrement(record);
		}
		Assert.isTrue(EnergyConsumptionRecordEntity.TEST.RECORDS.length == repository
				.count());
	}

}
