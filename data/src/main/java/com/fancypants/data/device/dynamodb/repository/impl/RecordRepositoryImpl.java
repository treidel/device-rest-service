package com.fancypants.data.device.dynamodb.repository.impl;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityInformation;
import org.socialsignin.spring.data.dynamodb.repository.support.EnableScanAnnotationPermissions;
import org.socialsignin.spring.data.dynamodb.repository.support.SimpleDynamoDBPagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;

@Component
public class RecordRepositoryImpl extends
		SimpleDynamoDBPagingAndSortingRepository<RecordEntity, RecordId>
		implements RecordRepository {

	public RecordRepositoryImpl(DynamoDBOperations dynamoDBOperations,
			DynamoDBEntityInformation<RecordEntity, RecordId> entityInformation) {
		super(entityInformation, dynamoDBOperations,
				new EnableScanAnnotationPermissions(RecordRepository.class));
	}

	@Override
	public Page<RecordEntity> findByDevice(String device, Pageable pageable) {
		RecordEntity record = new RecordEntity();
		record.setDevice(device);
		DynamoDBQueryExpression<RecordEntity> queryExpression = new DynamoDBQueryExpression<RecordEntity>()
				.withHashKeyValues(record);
		List<RecordEntity> records = dynamoDBOperations.query(
				RecordEntity.class, queryExpression);
		return new PageImpl<RecordEntity>(records);
	}

	@Override
	public Page<RecordEntity> findByDeviceAndTimestampRange(
			String timestamp_low, String timestamp_high) {
		// TODO Auto-generated method stub
		return null;
	}

}
