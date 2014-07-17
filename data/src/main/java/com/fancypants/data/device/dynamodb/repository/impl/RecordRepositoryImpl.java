package com.fancypants.data.device.dynamodb.repository.impl;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityMetadataSupport;
import org.socialsignin.spring.data.dynamodb.repository.support.EnableScanAnnotationPermissions;
import org.socialsignin.spring.data.dynamodb.repository.support.SimpleDynamoDBPagingAndSortingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;

@Component
public class RecordRepositoryImpl extends
		SimpleDynamoDBPagingAndSortingRepository<RecordEntity, RecordId>
		implements RecordRepository {

	@Autowired
	public RecordRepositoryImpl(DynamoDBOperations dynamoDBOperations) {
		super(new DynamoDBEntityMetadataSupport<RecordEntity, RecordId>(
				RecordEntity.class).getEntityInformation(), dynamoDBOperations,
				new EnableScanAnnotationPermissions(RecordRepository.class));
	}

	@Override
	public List<RecordEntity> findByDevice(String device) {
		RecordEntity record = new RecordEntity();
		record.setDevice(device);
		DynamoDBQueryExpression<RecordEntity> queryExpression = new DynamoDBQueryExpression<RecordEntity>()
				.withHashKeyValues(record);
		List<RecordEntity> records = dynamoDBOperations.query(
				RecordEntity.class, queryExpression);
		return records;
	}

	@Override
	public List<RecordEntity> findByDeviceAndTimestampRange(
			String timestamp_low, String timestamp_high) {
		// TODO Auto-generated method stub
		return null;
	}

}
