package com.fancypants.data.device.dynamodb.test.cases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

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
import com.fancypants.data.device.dynamodb.repository.DynamoDBRawRecordRepository;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.entity.RawRecordId;
import com.fancypants.data.device.repository.RawRecordRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {DynamoDBConfig.class, DynamoDBRawRecordRepository.class})
public class RawRecordTests extends AbstractTest {

	private static final RawRecordEntity RECORD1; 
	private static final RawRecordEntity RECORD2; 
	private static final RawRecordId INVALID_RECORD_ID;
	private static final Collection<RawRecordEntity> RECORDS = new ArrayList<RawRecordEntity>(
			2);

	private @Autowired
	RawRecordRepository repository;

	static {
		// setup test data
		Map<Integer, Float> energy = new TreeMap<Integer, Float>();
		for (int i = 1; i <= 16; i++) {
			energy.put(i, 10.0f);
		}
		// setup the test records
		RECORD1 = new RawRecordEntity("ABC1234", UUID.randomUUID().toString(), new Date(), energy);  
		RECORD2 = new RawRecordEntity("ABC1234", UUID.randomUUID().toString(), new Date(), energy);
		INVALID_RECORD_ID = new RawRecordId("XYZ789", UUID.randomUUID());

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
		Assert.isTrue(repository.insert(RECORD1));
	}

	@Test
	public void duplicateCreateTest() {
		// pre-create
		createTest();
		// now create again and make sure duplicate is detected
		Assert.isTrue(false == repository.insert(RECORD1));		
	}

	@Test
	public void querySuccessTest() {
		// run the create test to create a record
		createTest();
		// query for it
		RawRecordEntity record = repository.findOne(RECORD1.getId());
		Assert.isTrue(null != record);
	}

	@Test
	public void queryInvalidTest() {
		RawRecordEntity record = repository.findOne(INVALID_RECORD_ID);
		Assert.isNull(record);
	}

	@Test
	public void bulkInsertTest() {
		for (RawRecordEntity record : RECORDS) {
			repository.insert(record);
		}
		Assert.isTrue(RECORDS.size() == repository.count());
	}

}
