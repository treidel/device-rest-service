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
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordId;
import com.fancypants.data.device.dynamodb.repository.RawRecordRepository;

@Component
public class RawRecordRepositoryImpl extends
		AbstractRepositoryImpl<RawRecordEntity, RawRecordId> implements
		RawRecordRepository {

	@Autowired
	public RawRecordRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
		super(new DynamoDBMapper(amazonDynamoDB), RawRecordEntity.class);
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
			getDynamoDBMapper().save(record, expression);
		} catch (ConditionalCheckFailedException e) {
			return false;
		}
		return true;
	}

	@Override
	public List<RawRecordEntity> findAllForDevice(String device) {
		DynamoDBQueryExpression<RawRecordEntity> expression = new DynamoDBQueryExpression<RawRecordEntity>();
		RawRecordEntity entity = new RawRecordEntity();
		entity.setDevice(device);
		expression.setHashKeyValues(entity);
		List<RawRecordEntity> records = getDynamoDBMapper().query(
				RawRecordEntity.class, expression);
		return records;
	}

	@Override
	public RawRecordEntity findOne(RawRecordId id) {
		return getDynamoDBMapper().load(RawRecordEntity.class, id.getDevice(), id.getUUID());
	}
	
}
