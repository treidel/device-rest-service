package com.fancypants.data.device.test.cases;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DynamoDBConfig.class)
public class RepositoryTests {

	private static final RecordEntity RECORD1 = new RecordEntity();
	private static final RecordEntity RECORD2 = new RecordEntity();
	private static final RecordId INVALID_RECORD_ID = new RecordId();
	private static final Collection<RecordEntity> RECORDS = new ArrayList<RecordEntity>(2);
	
	private @Autowired
	RecordRepository repository;
	
	private @Autowired
	DateFormat iso8601DateFormat;

	@Before
	public void setup() {
		// setup the test records
		RECORD1.setDevice("ABC1234");
		RECORD1.setUUID(UUID.randomUUID().toString());
		RECORD1.setTimestamp(iso8601DateFormat.format(new Date()));
		for (int i = RecordEntity.MIN_CIRCUIT; i <= RecordEntity.MAX_CIRCUIT; i++) {
			RECORD1.setCircuit(1, 10.0f);
		}
		
		RECORD2.setDevice("ABC1234");
		RECORD2.setUUID(UUID.randomUUID().toString());
		RECORD2.setTimestamp(iso8601DateFormat.format(new Date()));
		for (int i = RecordEntity.MIN_CIRCUIT; i <= RecordEntity.MAX_CIRCUIT; i++) {
			RECORD2.setCircuit(1, 20.0f);
		}
		
		INVALID_RECORD_ID.setDevice("XYZ789");
		INVALID_RECORD_ID.setUUID(UUID.randomUUID().toString());
		
		// setup the list of records
		RECORDS.add(RECORD1);
		RECORDS.add(RECORD2);
	}
	
	@After
	public void cleanup() {
		// remove all records just in case
		repository.deleteAll();
	}

	@Test
	public void createTest() {
		repository.save(RECORD1);
	}
	
	@Test
	public void duplicateCreateTest() {
		// pre-create
		createTest();
		// now create against + query
		querySuccessTest();
	}
	
	@Test
	public void querySuccessTest() {
		// run the create test to create a record 
		createTest();
		// query for it 
		RecordEntity record = repository.findOne(RECORD1.getRecordId());
		Assert.isTrue(null != record);
	}
	
	
	@Test
	public void queryInvalidTest() {
		RecordEntity record = repository.findOne(INVALID_RECORD_ID);
		Assert.isNull(record);
	}
	
	@Test
	public void bulkInsertTest() {
		repository.save(RECORDS);
		Assert.isTrue(RECORDS.size() == repository.count());
	}
	
	@Test
	public void bulkQueryTest() {
		// run the bulk insert test to create multiple records
		bulkInsertTest();
		// query for all records
		Collection<RecordEntity> records = repository.findByDevice(RECORD1.getDevice());
		Assert.isTrue(RECORDS.size() == records.size());
	}
	

}
