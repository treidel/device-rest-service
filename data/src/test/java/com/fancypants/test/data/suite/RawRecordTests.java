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

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.repository.RawRecordRepository;
import com.fancypants.test.data.config.TestDataConfig;
import com.fancypants.test.data.values.RawRecordValues;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestDataConfig.class)
public class RawRecordTests {

	private @Autowired
	RawRecordRepository repository;

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
		Assert.isTrue(repository.insert(RawRecordValues.RECORD1));
	}

	@Test
	public void duplicateCreateTest() {
		// pre-create
		createTest();
		// now create again and make sure duplicate is detected
		Assert.isTrue(false == repository.insert(RawRecordValues.RECORD1));
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		createTest();
		// query for it
		RawRecordEntity record = repository.findOne(RawRecordValues.RECORD1
				.getId());
		Assert.isTrue(null != record);
	}

	@Test
	public void queryInvalidTest() {
		RawRecordEntity record = repository
				.findOne(RawRecordValues.INVALID_RECORD_ID);
		Assert.isNull(record);
	}

	@Test
	public void bulkInsertTest() {
		for (RawRecordEntity record : RawRecordValues.RECORDS) {
			repository.insert(record);
		}
		Assert.isTrue(RawRecordValues.RECORDS.length == repository.count());
	}

}
