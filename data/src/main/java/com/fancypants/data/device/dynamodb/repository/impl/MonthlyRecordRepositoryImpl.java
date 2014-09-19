package com.fancypants.data.device.dynamodb.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.repository.MonthlyRecordRepository;

@Component
public class MonthlyRecordRepositoryImpl implements MonthlyRecordRepository {

	private final DynamoDBMapper dynamoDBMapper;

	@Autowired
	public MonthlyRecordRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
		this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
	}

	@Override
	public List<PowerConsumptionRecordEntity> findByDevice(String device) {
		PowerConsumptionRecordEntity record = new PowerConsumptionRecordEntity();
		record.setDevice(device);
		DynamoDBQueryExpression<PowerConsumptionRecordEntity> queryExpression = new DynamoDBQueryExpression<PowerConsumptionRecordEntity>()
				.withHashKeyValues(record);
		List<PowerConsumptionRecordEntity> records = dynamoDBMapper.query(
				PowerConsumptionRecordEntity.class, queryExpression);
		return records;
	}

	@Override
	public void insertOrIncrement(PowerConsumptionRecordEntity record) {
		DynamoDBSaveExpression expression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
		ExpectedAttributeValue expectedValue = new ExpectedAttributeValue(
				new AttributeValue());
		expectedValue.setComparisonOperator(ComparisonOperator.NULL);
		expected.put(RawRecordEntity.HASH_KEY, expectedValue);
		expression.setExpected(expected);
		dynamoDBMapper.save(record, expression);
	
	}

	@Override
	public void deleteAll() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		List<RawRecordEntity> records = dynamoDBMapper.scan(
				RawRecordEntity.class, expression);
		dynamoDBMapper.batchDelete(records);
	}

	@Override
	public int count() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		return dynamoDBMapper.count(RawRecordEntity.class, expression);
	}
}
