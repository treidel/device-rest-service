package com.fancypants.data.device.dynamodb.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;

@Component
public class RecordRepositoryImpl implements RecordRepository {

	private final DynamoDBMapper dynamoDBMapper;

	@Autowired
	public RecordRepositoryImpl(DynamoDBMapper dynamoDBMapper) {
		this.dynamoDBMapper = dynamoDBMapper;
	}

	@Override
	public List<RecordEntity> findByDevice(String device) {
		RecordEntity record = new RecordEntity();
		record.setDevice(device);
		DynamoDBQueryExpression<RecordEntity> queryExpression = new DynamoDBQueryExpression<RecordEntity>()
				.withHashKeyValues(record);
		List<RecordEntity> records = dynamoDBMapper.query(RecordEntity.class,
				queryExpression);
		return records;
	}

	@Override
	public boolean insert(RecordEntity record) {
		DynamoDBSaveExpression expression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
		ExpectedAttributeValue expectedValue = new ExpectedAttributeValue(
				new AttributeValue());
		expectedValue.setComparisonOperator(ComparisonOperator.NULL);
		expected.put(RecordEntity.HASH_KEY, expectedValue);
		expression.setExpected(expected);
		try {
			dynamoDBMapper.save(record, expression);
		} catch (ConditionalCheckFailedException e) {
			return false;
		}
		return true;
	}

	@Override
	public void deleteAll() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		List<RecordEntity> records = dynamoDBMapper.scan(RecordEntity.class, expression);
		dynamoDBMapper.batchDelete(records);
	}

	@Override
	public RecordEntity get(RecordId recordId) {
		return dynamoDBMapper.load(RecordEntity.class, recordId.getDevice(), recordId.getUUID());
	}

	@Override
	public int count() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		return dynamoDBMapper.count(RecordEntity.class, expression);
	}
}
