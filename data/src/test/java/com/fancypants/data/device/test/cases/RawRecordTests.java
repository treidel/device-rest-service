package com.fancypants.data.device.test.cases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

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
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordId;
import com.fancypants.data.device.dynamodb.repository.RawRecordRepository;
import com.fancypants.data.device.dynamodb.repository.impl.RawRecordRepositoryImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {DynamoDBConfig.class, RawRecordRepositoryImpl.class})
public class RawRecordTests extends AbstractTest {

	private static final RawRecordEntity RECORD1 = new RawRecordEntity();
	private static final RawRecordEntity RECORD2 = new RawRecordEntity();
	private static final RawRecordId INVALID_RECORD_ID = new RawRecordId();
	private static final Collection<RawRecordEntity> RECORDS = new ArrayList<RawRecordEntity>(
			2);

	private @Autowired
	RawRecordRepository repository;

	@BeforeClass
	public static void init() {
		// setup the test records
		RECORD1.setDevice("ABC1234");
		RECORD1.setUUID(UUID.randomUUID().toString());
		RECORD1.setTimestamp(iso8601DateFormat.format(new Date()));
		for (int i = RawRecordEntity.MIN_CIRCUIT; i <= RawRecordEntity.MAX_CIRCUIT; i++) {
			RECORD1.setCircuit(i, 10.0f);
		}

		RECORD2.setDevice("ABC1234");
		RECORD2.setUUID(UUID.randomUUID().toString());
		RECORD2.setTimestamp(iso8601DateFormat.format(new Date()));
		for (int i = RawRecordEntity.MIN_CIRCUIT; i <= RawRecordEntity.MAX_CIRCUIT; i++) {
			RECORD2.setCircuit(i, 20.0f);
		}

		INVALID_RECORD_ID.setDevice("XYZ789");
		INVALID_RECORD_ID.setUUID(UUID.randomUUID().toString());

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
		RawRecordEntity record = repository.get(RECORD1.getRecordId());
		Assert.isTrue(null != record);
	}

	@Test
	public void queryInvalidTest() {
		RawRecordEntity record = repository.get(INVALID_RECORD_ID);
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
