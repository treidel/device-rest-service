package com.fancypants.data.device.dynamodb.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.repository.DeviceRepository;

@Component
public class DynamoDBDeviceRepository extends
		SimpleDynamoDBRepository<DeviceEntity, String> implements
		DeviceRepository {
	private static final Logger LOG = LoggerFactory
			.getLogger(DynamoDBDeviceRepository.class);

	private static final long serialVersionUID = 5702619783287693597L;
	private static final String TABLE_NAME = "devices";

	public DynamoDBDeviceRepository() {
		super(DeviceEntity.class);
		LOG.trace("DynamoDBDeviceRepository enter");
		LOG.trace("DynamoDBDeviceRepository exit");
	}

	@Override
	protected String retrieveTableName() {
		return TABLE_NAME;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(DeviceEntity entity) {
		LOG.trace("retrievePrimaryKey enter", "entity", entity);
		String hash = entity.getDevice();
		PrimaryKey primaryKey = new PrimaryKey(DeviceEntity.DEVICE_ATTRIBUTE,
				hash);
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(String id) {
		LOG.trace("retrievePrimaryKey enter", "id", id);
		PrimaryKey primaryKey = new PrimaryKey(DeviceEntity.DEVICE_ATTRIBUTE,
				id);
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(Item item) {
		LOG.trace("retrievePrimaryKey enter", "item", item);
		PrimaryKey primaryKey = new PrimaryKey(DeviceEntity.DEVICE_ATTRIBUTE,
				item.getString(DeviceEntity.DEVICE_ATTRIBUTE));
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}
}
