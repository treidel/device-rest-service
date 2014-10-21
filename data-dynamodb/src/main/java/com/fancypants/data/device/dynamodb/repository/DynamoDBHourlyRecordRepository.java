package com.fancypants.data.device.dynamodb.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.entity.PowerConsumptionRecordId;
import com.fancypants.data.device.repository.HourlyRecordRepository;

@Component
public class DynamoDBHourlyRecordRepository
		extends
		AbstractDynamoDBRepository<PowerConsumptionRecordEntity, PowerConsumptionRecordId>
		implements HourlyRecordRepository {

	@Autowired
	public DynamoDBHourlyRecordRepository(AmazonDynamoDB amazonDynamoDB) {
		super(amazonDynamoDB, PowerConsumptionRecordEntity.class,
				PowerConsumptionRecordEntity.TABLE_NAME,
				PowerConsumptionRecordEntity.HASH_KEY,
				PowerConsumptionRecordEntity.RANGE_KEY);
	}

	@Override
	protected String retrieveHashKey(PowerConsumptionRecordEntity entity) {
		return entity.getDevice();
	}
	
	@Override
	protected String retrieveHashKey(PowerConsumptionRecordId id) {
		return id.getDevice();
	}

	@Override
	protected String retrieveRangeKey(PowerConsumptionRecordEntity entity) {
		return getObjectMapper().getDeserializationConfig().getDateFormat()
				.format(entity.getDate());
	}
	
	@Override
	protected String retrieveRangeKey(PowerConsumptionRecordId id) {
		return getObjectMapper().getDeserializationConfig().getDateFormat()
				.format(id.getDate());
	}

	@Override
	public List<PowerConsumptionRecordEntity> findByDevice(String device) {
		KeyAttribute key = new KeyAttribute(
				PowerConsumptionRecordEntity.DEVICE_ATTRIBUTE, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		List<PowerConsumptionRecordEntity> entities = new LinkedList<PowerConsumptionRecordEntity>();
		for (Item item : items) {
			PowerConsumptionRecordEntity entity = deserialize(item);
			entities.add(entity);
		}
		return entities;
	}
	
	@Override
	public void deleteAllForDevice(String device) {
		KeyAttribute key = new KeyAttribute(
				PowerConsumptionRecordEntity.DEVICE_ATTRIBUTE, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		for (Item item : items) {
			PowerConsumptionRecordEntity entity = deserialize(item);
			delete(entity);
		}
	}

	@Override
	public void insertOrIncrement(PowerConsumptionRecordEntity record) {
		// create the primary key
		PrimaryKey key = new PrimaryKey(PowerConsumptionRecordEntity.HASH_KEY,
				record.getId().getDevice(), 
				PowerConsumptionRecordEntity.RANGE_KEY, getObjectMapper()
						.getSerializationConfig().getDateFormat()
						.format(record.getId().getDate()));
		// create the update
		UpdateItemSpec spec = new UpdateItemSpec().withPrimaryKey(key);
		// populate the measurement attributes
		for (Map.Entry<Integer, Float> entry : record.getEnergy().entrySet()) {
			AttributeUpdate update = new AttributeUpdate(
					PowerConsumptionRecordEntity.ENERGY_IN_KWH_ATTRIBUTE_PREFIX
							+ entry.getKey());
			update.addNumeric(entry.getValue());
			spec.addAttributeUpdate(update);
		}
		// do the update
		getTable().updateItem(spec);
	}
}
