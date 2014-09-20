package com.fancypants.data.device.dynamodb.repository.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.dynamodb.repository.MonthlyRecordRepository;

@Component
public class MonthlyRecordRepositoryImpl implements MonthlyRecordRepository {

	private final AmazonDynamoDB amazonDynamoDB;
	private final DynamoDBMapper dynamoDBMapper;

	@Autowired
	public MonthlyRecordRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
		this.amazonDynamoDB = amazonDynamoDB;
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
		// the DynamoDBMapper doesn't to the ADD operation of simple attributes so we manually handle this operation
		// via an UpdateItem operation
		UpdateItemRequest request = new UpdateItemRequest();
		// TBD: dynamic table name
		request.setTableName( PowerConsumptionRecordEntity.TABLE_NAME);
		// set the key attributes
		request.addKeyEntry(PowerConsumptionRecordEntity.HASH_KEY, new AttributeValue(record.getDevice()));
		request.addKeyEntry(PowerConsumptionRecordEntity.RANGE_KEY, new AttributeValue(record.getDate()));
		// populate the measurement attributes
		for (Map.Entry<Integer, Float> entry : record.getMeasurements().entrySet()) {
			AttributeValue attributeValue = new AttributeValue().withN(entry.getValue().toString());
			AttributeValueUpdate attributeUpdate = new AttributeValueUpdate(attributeValue, AttributeAction.ADD);
			request.addAttributeUpdatesEntry(PowerConsumptionRecordEntity.MEASUREMENT_ATTRIBUTE_PREFIX + entry.getKey(), attributeUpdate);
		}
		// do the update, we don't care about the response 
		amazonDynamoDB.updateItem(request);
	}

	@Override
	public void deleteAll() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		List<PowerConsumptionRecordEntity> records = dynamoDBMapper.scan(
				PowerConsumptionRecordEntity.class, expression);
		dynamoDBMapper.batchDelete(records);
	}

	@Override
	public int count() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		return dynamoDBMapper.count(PowerConsumptionRecordEntity.class, expression);
	}
 
}
