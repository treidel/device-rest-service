package com.fancypants.data.device.dynamodb.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.repository.DeviceRepository;

@Component
public class DynamodBDeviceRepository extends
		AbstractDynamoDBRepository<DeviceEntity, String> implements
		DeviceRepository {

	@Autowired
	public DynamodBDeviceRepository(AmazonDynamoDB amazonDynamoDB) {
		super(amazonDynamoDB, DeviceEntity.class, DeviceEntity.TABLE_NAME, DeviceEntity.HASH_ATTRIBUTE);
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
