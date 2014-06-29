package com.fancypants.data.device.test.cases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DynamoDBConfig.class)
public class RepositoryTests {

	private static final RecordEntity RECORD1 = new RecordEntity();
	private static final RecordEntity RECORD2 = new RecordEntity();
	private static final Collection<RecordEntity> RECORDS = new ArrayList<RecordEntity>(2);
	
	private static DateFormat df;
	
	private @Autowired
	RecordRepository repository;

	@BeforeClass
	public static void setup() {
		// setup ISO8601/RFC3339 time formatter
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		// setup the test records
		RECORD1.setDevice("ABC1234");
		RECORD1.setUUID(UUID.randomUUID().toString());
		RECORD2.setDevice("ABC1234");
		RECORD2.setUUID(UUID.randomUUID().toString());
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
		repository.save(RECORD1);
		// save again
		repository.save(RECORD1);
	}
	
	@Test
	public void queryTest() {
		// create a record
		repository.save(RECORD1);
		// query for it 
		RecordEntity record = repository.findOne(RECORD1.getRecordId());
		Assert.isNull(record);
	}
	
	@Test
	public void bulkInsertTest() {
		repository.save(RECORDS);
		Assert.isTrue(RECORDS.size() == repository.count());
	}
}
