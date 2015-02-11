package com.fancypants.data.device.dynamodb.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordId;
import com.fancypants.data.repository.HourlyRecordRepository;

@Component
public class DynamoDBHourlyRecordRepository
		extends
		AbstractDynamoDBRepository<EnergyConsumptionRecordEntity, EnergyConsumptionRecordId>
		implements HourlyRecordRepository {

	private static final long serialVersionUID = -1966466045228650857L;
	private static final String TABLE_NAME = "hourly";

	public DynamoDBHourlyRecordRepository() {
		super(EnergyConsumptionRecordEntity.class,
				EnergyConsumptionRecordEntity.HASH_KEY,
				EnergyConsumptionRecordEntity.RANGE_KEY);
	}

	@Override
	protected String retrieveTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String retrieveHashKey(EnergyConsumptionRecordEntity entity) {
		return entity.getDevice();
	}

	@Override
	protected String retrieveHashKey(EnergyConsumptionRecordId id) {
		return id.getDevice();
	}

	@Override
	protected String retrieveRangeKey(EnergyConsumptionRecordEntity entity) {
		return getObjectMapper().getDeserializationConfig().getDateFormat()
				.format(entity.getDate());
	}

	@Override
	protected String retrieveRangeKey(EnergyConsumptionRecordId id) {
		return getObjectMapper().getDeserializationConfig().getDateFormat()
				.format(id.getDate());
	}

	@Override
	public List<EnergyConsumptionRecordEntity> findByDevice(String device) {
		KeyAttribute key = new KeyAttribute(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		List<EnergyConsumptionRecordEntity> entities = new LinkedList<EnergyConsumptionRecordEntity>();
		for (Item item : items) {
			EnergyConsumptionRecordEntity entity = deserialize(item);
			entities.add(entity);
		}
		return entities;
	}

	@Override
	public void insertOrIncrement(EnergyConsumptionRecordEntity record) {
		// create the primary key
		PrimaryKey key = new PrimaryKey(EnergyConsumptionRecordEntity.HASH_KEY,
				record.getId().getDevice(),
				EnergyConsumptionRecordEntity.RANGE_KEY, getObjectMapper()
						.getSerializationConfig().getDateFormat()
						.format(record.getId().getDate()));
		// create the update
		UpdateItemSpec spec = new UpdateItemSpec().withPrimaryKey(key);
		// populate the measurement attributes
		for (Map.Entry<Integer, Float> entry : record.getEnergy().entrySet()) {
			AttributeUpdate update = new AttributeUpdate(
					EnergyConsumptionRecordEntity.ENERGY_IN_KWH_ATTRIBUTE_PREFIX
							+ entry.getKey());
			update.addNumeric(entry.getValue());
			spec.addAttributeUpdate(update);
		}
		// do the update
		getTable().updateItem(spec);
	}

	@Override
	public void deleteAllForDevice(String device) {
		KeyAttribute key = new KeyAttribute(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		for (Item item : items) {
			EnergyConsumptionRecordEntity entity = deserialize(item);
			delete(entity);
		}
	}
}
