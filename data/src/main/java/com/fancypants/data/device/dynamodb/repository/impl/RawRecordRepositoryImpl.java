package com.fancypants.data.device.dynamodb.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordId;
import com.fancypants.data.device.dynamodb.repository.RawRecordRepository;

@Component
public class RawRecordRepositoryImpl implements RawRecordRepository {

	private final DynamoDBMapper dynamoDBMapper;

	@Autowired
	public RawRecordRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
		this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
	}

	@Override
	public boolean insert(RawRecordEntity record) {
		DynamoDBSaveExpression expression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
		ExpectedAttributeValue expectedValue = new ExpectedAttributeValue();
		expectedValue.setComparisonOperator(ComparisonOperator.NULL);
		expected.put(RawRecordEntity.HASH_KEY, expectedValue);
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
		List<RawRecordEntity> records = dynamoDBMapper.scan(RawRecordEntity.class,
				expression);
		dynamoDBMapper.batchDelete(records);
	}

	@Override
	public RawRecordEntity get(RawRecordId recordId) {
		return dynamoDBMapper.load(RawRecordEntity.class, recordId);
	}

	@Override
	public int count() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		return dynamoDBMapper.count(RawRecordEntity.class, expression);
	}
}
