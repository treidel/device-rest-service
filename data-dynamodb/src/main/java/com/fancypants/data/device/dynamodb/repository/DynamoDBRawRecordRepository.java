package com.fancypants.data.device.dynamodb.repository;

import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.data.repository.RawRecordRepository;

@Component
public class DynamoDBRawRecordRepository extends
		AbstractDynamoDBRepository<RawRecordEntity, RawRecordId> implements
		RawRecordRepository {

	private static final long serialVersionUID = -6669041689916234705L;
	private static final String TABLE_NAME = "raw";

	public DynamoDBRawRecordRepository() {
		super(RawRecordEntity.class, RawRecordEntity.HASH_KEY);
	}

	@Override
	protected String retrieveTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String retrieveHashKey(RawRecordEntity entity) {
		return retrieveHashKey(entity.getId());
	}

	@Override
	protected String retrieveHashKey(RawRecordId id) {
		return id.getDevice() + ":" + id.getUUID().toString();
	}

	@Override
	protected String retrieveRangeKey(RawRecordEntity entity) {
		return null;
	}

	@Override
	protected String retrieveRangeKey(RawRecordId id) {
		return null;
	}

	@Override
	public boolean insert(RawRecordEntity record) {
		// serialize the record
		Item item = serialize((RawRecordEntity) record);
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
}
