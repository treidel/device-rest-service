package com.fancypants.data.device.dynamodb.repository;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
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
public class DynamoDBRawRecordRepository extends
		AbstractDynamoDBRepository<RawRecordEntity, RawRecordId> implements
		RawRecordRepository {

	@Autowired
	public DynamoDBRawRecordRepository(AmazonDynamoDB amazonDynamoDB) {
		super(amazonDynamoDB, RawRecordEntity.class,
				RawRecordEntity.TABLE_NAME, RawRecordEntity.HASH_KEY,
				RawRecordEntity.RANGE_KEY);
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

}
