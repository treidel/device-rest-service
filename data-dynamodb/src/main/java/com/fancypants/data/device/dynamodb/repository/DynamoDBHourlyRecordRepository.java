package com.fancypants.data.device.dynamodb.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		SimpleDynamoDBRepository<EnergyConsumptionRecordEntity, EnergyConsumptionRecordId>
		implements HourlyRecordRepository {

	private static final Logger LOG = LoggerFactory
			.getLogger(DynamoDBHourlyRecordRepository.class);

	private static final long serialVersionUID = -1966466045228650857L;
	private static final String TABLE_NAME = "hourly";

	public DynamoDBHourlyRecordRepository() {
		super(EnergyConsumptionRecordEntity.class);
		LOG.trace("DynamoDBHourlyRecordRepository enter");
		LOG.trace("DynamoDBHourlyRecordRepository exit");
	}

	@Override
	protected String retrieveTableName() {
		return TABLE_NAME;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(EnergyConsumptionRecordEntity entity) {
		LOG.trace("retrievePrimaryKey enter", "entity", entity);
		String hash = entity.getDevice();
		String range = getObjectMapper().getDeserializationConfig()
				.getDateFormat().format(entity.getDate());
		PrimaryKey primaryKey = new PrimaryKey(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE, hash,
				EnergyConsumptionRecordEntity.DATE_ATTRIBUTE, range);
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(EnergyConsumptionRecordId id) {
		LOG.trace("retrievePrimaryKey enter", "id", id);
		String hash = id.getDevice();
		String range = getObjectMapper().getDeserializationConfig()
				.getDateFormat().format(id.getDate());
		PrimaryKey primaryKey = new PrimaryKey(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE, hash,
				EnergyConsumptionRecordEntity.DATE_ATTRIBUTE, range);
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(Item item) {
		LOG.trace("retrievePrimaryKey enter", "item", item);
		PrimaryKey primaryKey = new PrimaryKey(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE,
				item.getString(EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE),
				EnergyConsumptionRecordEntity.DATE_ATTRIBUTE,
				item.getString(EnergyConsumptionRecordEntity.DATE_ATTRIBUTE));
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	public List<EnergyConsumptionRecordEntity> findByDevice(String device) {
		LOG.trace("findByDevice enter", "device", device);
		KeyAttribute key = new KeyAttribute(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		List<EnergyConsumptionRecordEntity> entities = new LinkedList<EnergyConsumptionRecordEntity>();
		for (Item item : items) {
			EnergyConsumptionRecordEntity entity = deserialize(item);
			entities.add(entity);
		}
		LOG.trace("findByDevice exit", entities);
		return entities;
	}

	@Override
	public void insertOrIncrement(EnergyConsumptionRecordEntity record) {
		LOG.trace("insertOrIncrement enter", "record", record);
		// create the primary key
		PrimaryKey key = retrievePrimaryKey(record);
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
		LOG.trace("insertOrIncrement exit");
	}

	@Override
	public void deleteAllForDevice(String device) {
		LOG.trace("deleteAllForDevice enter", "device", device);
		KeyAttribute key = new KeyAttribute(
				EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		for (Item item : items) {
			EnergyConsumptionRecordEntity entity = deserialize(item);
			delete(entity);
		}
		LOG.trace("deleteAllForDevice exit");
	}
}
