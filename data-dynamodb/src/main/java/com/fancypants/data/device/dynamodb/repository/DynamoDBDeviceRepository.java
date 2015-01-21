package com.fancypants.data.device.dynamodb.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;

@Component
@Lazy
public class DynamoDBDeviceRepository extends
		AbstractDynamoDBRepository<DeviceEntity, String> implements
		DeviceRepository {

	private static final long serialVersionUID = 5702619783287693597L;
	private static final String TABLE_NAME = "devices";

	@Autowired
	public DynamoDBDeviceRepository(AmazonDynamoDB amazonDynamoDB) {
		super(amazonDynamoDB, DeviceEntity.class, DeviceEntity.HASH_ATTRIBUTE);
	}

	@Override
	protected String retrieveTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String retrieveHashKey(DeviceEntity entity) {
		return entity.getDevice();
	}

	@Override
	protected String retrieveHashKey(String id) {
		return id;
	}

	@Override
	protected String retrieveRangeKey(DeviceEntity entity) {
		return null;
	}

	@Override
	protected String retrieveRangeKey(String id) {
		return null;
	}

}
