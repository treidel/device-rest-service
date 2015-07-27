package com.fancypants.test.data.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.data.partitioner.RawRecordPartitioner;
import com.fancypants.data.repository.RawRecordRepository;
import com.fancypants.test.data.config.TestDataConfig;
import com.fancypants.test.data.values.RawRecordValues;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestDataConfig.class)
public class RawRecordTests {

	private @Autowired
	RawRecordRepository repository;

	private @Autowired
	RawRecordPartitioner partitioner;

	@Before
	public void setup() {
		// make sure a partition exists for all records
		for (RawRecordEntity entity : RawRecordValues.RECORDS) {
			String partition = partitioner.partition(entity);
			repository.createPartition(partition);
		}

	}

	@After
	public void cleanup() {
		// clean up all partitions
		for (RawRecordEntity entity : RawRecordValues.RECORDS) {
			String partition = partitioner.partition(entity);
			repository.deletePartition(partition);
		}
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
		String partition = partitioner.partition(RawRecordValues.RECORD1);
		CrudRepository<RawRecordEntity, RawRecordId> crudRepository = repository
				.retrievePartitionTable(partition);
		RawRecordEntity record = crudRepository.findOne(RawRecordValues.RECORD1
				.getId());
		Assert.isTrue(null != record);
	}

	@Test
	public void bulkInsertTest() {
		for (RawRecordEntity record : RawRecordValues.RECORDS) {
			repository.insert(record);
		}
	}

}
