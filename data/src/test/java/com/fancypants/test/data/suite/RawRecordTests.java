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

import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.repository.RawRecordRepository;
import com.fancypants.test.data.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestConfig.class)
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
		Assert.isTrue(repository.insert(RawRecordEntity.TEST.RECORD1));
	}

	@Test
	public void duplicateCreateTest() {
		// pre-create
		createTest();
		// now create again and make sure duplicate is detected
		Assert.isTrue(false == repository.insert(RawRecordEntity.TEST.RECORD1));
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		createTest();
		// query for it
		RawRecordEntity record = repository
				.findOne(RawRecordEntity.TEST.RECORD1.getId());
		Assert.isTrue(null != record);
	}

	@Test
	public void queryInvalidTest() {
		RawRecordEntity record = repository
				.findOne(RawRecordEntity.TEST.INVALID_RECORD_ID);
		Assert.isNull(record);
	}

	@Test
	public void bulkInsertTest() {
		for (RawRecordEntity record : RawRecordEntity.TEST.RECORDS) {
			repository.insert(record);
		}
		Assert.isTrue(RawRecordEntity.TEST.RECORDS.length == repository.count());
	}

}
