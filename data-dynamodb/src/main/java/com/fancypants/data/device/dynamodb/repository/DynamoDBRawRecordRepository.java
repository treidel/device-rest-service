package com.fancypants.data.device.dynamodb.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.data.partitioner.RawRecordPartitioner;
import com.fancypants.data.repository.RawRecordRepository;

@Component
public class DynamoDBRawRecordRepository extends PartitionedDynamoDBRepository<RawRecordEntity, RawRecordId, Date>
		implements RawRecordRepository {

	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBRawRecordRepository.class);

	private static final long serialVersionUID = -6669041689916234705L;
	private static final String TABLE_NAME = "raw";
	private static final Collection<KeySchemaElement> KEY_SCHEMA = Arrays
			.asList(new KeySchemaElement(RawRecordEntity.RECORDID_ATTRIBUTE, KeyType.HASH));

	@Autowired
	public DynamoDBRawRecordRepository(RawRecordPartitioner partitioner) {
		super(RawRecordEntity.class, partitioner);
		LOG.trace("PartitionedDynamoDBRepository enter");
		LOG.trace("PartitionedDynamoDBRepository exit");
	}

	@Override
	protected String retrieveBaseTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Collection<KeySchemaElement> retrieveKeySchema() {
		return KEY_SCHEMA;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(RawRecordEntity entity) {
		LOG.trace("retrievePrimaryKey enter", "entity", entity);
		PrimaryKey primaryKey = retrievePrimaryKey(entity.getId());
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(RawRecordId id) {
		LOG.trace("retrievePrimaryKey enter", "id", id);
		String hash = id.getDevice() + ":" + id.getUUID().toString();
		PrimaryKey primaryKey = new PrimaryKey(RawRecordEntity.RECORDID_ATTRIBUTE, hash);
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	protected PrimaryKey retrievePrimaryKey(Item item) {
		LOG.trace("retrievePrimaryKey enter", "item", item);
		PrimaryKey primaryKey = new PrimaryKey(RawRecordEntity.RECORDID_ATTRIBUTE,
				item.getString(RawRecordEntity.RECORDID_ATTRIBUTE));
		LOG.trace("retrievePrimaryKey exit", primaryKey);
		return primaryKey;
	}

	@Override
	public boolean insert(RawRecordEntity record) {
		// serialize the record
		Item item = serialize((RawRecordEntity) record);
		// setup the expected value
		Expected expected = new Expected(RawRecordEntity.HASH_KEY).notExist();

		try {
			// get the table
			Table table = getTable(record);
			// run the query
			table.putItem(item, expected);
		} catch (ConditionalCheckFailedException e) {
			return false;
		}
		return true;
	}
}
