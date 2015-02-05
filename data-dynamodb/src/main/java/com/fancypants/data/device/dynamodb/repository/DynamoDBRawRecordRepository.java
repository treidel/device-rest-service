package com.fancypants.data.device.dynamodb.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.entity.RawRecordId;
import com.fancypants.data.device.repository.RawRecordRepository;

@Component
@Lazy
public class DynamoDBRawRecordRepository extends
		AbstractDynamoDBRepository<RawRecordEntity, RawRecordId> implements
		RawRecordRepository {

	private static final long serialVersionUID = -6669041689916234705L;
	private static final String TABLE_NAME = "raw";

	public DynamoDBRawRecordRepository() {
		super(RawRecordEntity.class, RawRecordEntity.HASH_KEY,
				RawRecordEntity.RANGE_KEY);
	}

	@Override
	protected String retrieveTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String retrieveHashKey(RawRecordEntity entity) {
		return entity.getDevice();
	}

	@Override
	protected String retrieveHashKey(RawRecordId id) {
		return id.getDevice();
	}

	@Override
	protected String retrieveRangeKey(RawRecordEntity entity) {
		return entity.getUUID().toString();
	}

	@Override
	protected String retrieveRangeKey(RawRecordId id) {
		return id.getUUID().toString();
	}

	@Override
	public boolean insert(RawRecordEntity record) {
		// serialize the record
		Item item = serialize(record);
		// setup the expected value
		Expected expected = new Expected(RawRecordEntity.HASH_KEY).notExist();
		// run the query
		try {
			getTable().putItem(item, expected);
		} catch (ConditionalCheckFailedException e) {
			return false;
		}
		return true;
	}

	@Override
	public List<RawRecordEntity> findAllForDevice(String device) {
		KeyAttribute key = new KeyAttribute(RawRecordEntity.HASH_KEY, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		List<RawRecordEntity> records = new LinkedList<RawRecordEntity>();
		for (Item item : items) {
			RawRecordEntity record = deserialize(item);
			records.add(record);
		}
		return records;
	}

	@Override
	public void deleteAllForDevice(String device) {
		KeyAttribute key = new KeyAttribute(RawRecordEntity.HASH_KEY, device);
		ItemCollection<QueryOutcome> items = getTable().query(key);
		for (Item item : items) {
			RawRecordId id = new RawRecordId(
					item.getString(RawRecordEntity.DEVICE_ATTRIBUTE),
					UUID.fromString(item
							.getString(RawRecordEntity.UUID_ATTRIBUTE)));
			delete(id);
		}
	}

}
