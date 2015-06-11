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

import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.repository.HourlyRecordRepository;
import com.fancypants.test.data.config.TestDataConfig;
import com.fancypants.test.data.values.HourlyRecordValues;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestDataConfig.class)
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
		repository.insertOrIncrement(HourlyRecordValues.RECORD1);
	}

	@Test
	public void updateTest() {
		// pre-create
		createTest();
		// now update
		repository.insertOrIncrement(HourlyRecordValues.RECORD1);
		// now query the row
		EnergyConsumptionRecordEntity record = repository
				.findOne(HourlyRecordValues.RECORD1.getId());
		// check the values - should be doubled
		float value1 = HourlyRecordValues.RECORD1.getEnergy(1);
		float value2 = record.getEnergy(1);
		Assert.isTrue(value1 == value2 / 2);
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		bulkInsertTest();
		// query for all records
		List<EnergyConsumptionRecordEntity> records = repository
				.findByDevice(HourlyRecordValues.RECORD1.getDevice());
		Assert.isTrue(HourlyRecordValues.RECORDS.length == records.size());
	}

	@Test
	public void bulkInsertTest() {
		for (EnergyConsumptionRecordEntity record : HourlyRecordValues.RECORDS) {
			repository.insertOrIncrement(record);
		}
		Assert.isTrue(HourlyRecordValues.RECORDS.length == repository.count());
	}

}
