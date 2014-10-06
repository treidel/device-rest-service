package com.fancypants.data.device.dynamodb.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;

@Component
public class DeviceRepositoryImpl extends
		AbstractRepositoryImpl<DeviceEntity, String> implements
		DeviceRepository {

	@Autowired
	public DeviceRepositoryImpl(AmazonDynamoDB amazonDynamoDB) {
		super(new DynamoDBMapper(amazonDynamoDB), DeviceEntity.class);
	}

	@Override
	public DeviceEntity findOne(String device) {
		return getDynamoDBMapper().load(DeviceEntity.class, device);
	}

}
