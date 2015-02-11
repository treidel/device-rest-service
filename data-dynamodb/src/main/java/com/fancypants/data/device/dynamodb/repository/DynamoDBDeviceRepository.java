package com.fancypants.data.device.dynamodb.repository;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.repository.DeviceRepository;

@Component
public class DynamoDBDeviceRepository extends
		AbstractDynamoDBRepository<DeviceEntity, String> implements
		DeviceRepository {

	private static final long serialVersionUID = 5702619783287693597L;
	private static final String TABLE_NAME = "devices";

	public DynamoDBDeviceRepository() {
		super(DeviceEntity.class, DeviceEntity.HASH_ATTRIBUTE);
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
