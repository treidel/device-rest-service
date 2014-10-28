package com.fancypants.data.device.dynamodb.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public abstract class AbstractDynamoDBRepository<T, I extends Serializable>
		implements CrudRepository<T, I>, Serializable {

	private static final long serialVersionUID = -3291793409692029613L;

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractDynamoDBRepository.class);

	private transient DynamoDB dynamoDB;
	private final Class<T> clazz;
	private final String hashAttribute;
	private final String rangeAttribute;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired(required = false)
	@Qualifier("tablePrefix")
	private String tablePrefix;

	public AbstractDynamoDBRepository(AmazonDynamoDB amazonDynamoDB,
			Class<T> clazz, String hashAttribute) {
		this(amazonDynamoDB, clazz, hashAttribute, null);
	}

	public AbstractDynamoDBRepository(AmazonDynamoDB amazonDynamoDB,
			Class<T> clazz, String hashAttribute, String rangeAttribute) {
		LOG.trace("AbstractDynamoDBRepository entry");

		// make sure this object is serializable
		if (false == objectMapper.canSerialize(clazz)) {
			LOG.error("invalid class", clazz);
			throw new IllegalAccessError("can not serialize class=" + clazz);
		}

		// configure the data serialization
		objectMapper.setDateFormat(new ISO8601DateFormat());

		// store variables
		this.dynamoDB = new DynamoDB(amazonDynamoDB);
		this.clazz = clazz;
		this.hashAttribute = hashAttribute;
		this.rangeAttribute = rangeAttribute;
		LOG.trace("AbstractDynamoDBRepository entry");
	}

	public void setDynamoDB(DynamoDB dynamoDB) {
		this.dynamoDB = dynamoDB;
	}

	protected DynamoDB getDynamoDB() {
		return dynamoDB;
	}

	protected Table getTable() {
		// get the base table name
		String baseTableName = retrieveTableName();

		if (null != tablePrefix) {
			return dynamoDB.getTable(tablePrefix + "_" + baseTableName);
		} else {
			return dynamoDB.getTable(baseTableName);
		}
	}

	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	protected T deserialize(Item item) {
		String json = item.toJSON();
		try {
			T entity = objectMapper.readValue(json, clazz);
			return entity;
		} catch (IOException e) {
			LOG.error("invalid json", json);
			return null;
		}
	}

	protected Item serialize(T entity) {
		try {
			String json = objectMapper.writeValueAsString(entity);
			Item item = Item.fromJSON(json);
			return item;
		} catch (JsonProcessingException e) {
			LOG.error("can't serialize", e);
			return null;
		}
	}

	protected abstract String retrieveTableName();

	protected abstract String retrieveHashKey(T entity);

	protected abstract String retrieveHashKey(I id);

	protected abstract String retrieveRangeKey(T entity);

	protected abstract String retrieveRangeKey(I id);

	@Override
	public void deleteAll() {
		Table table = getTable();
		ItemCollection<ScanOutcome> items = table.scan();
		for (Item item : items) {
			PrimaryKey key = new PrimaryKey(hashAttribute,
					item.getString(hashAttribute));
			if (null != rangeAttribute) {
				key.addComponent(rangeAttribute, item.getString(rangeAttribute));
			}
			table.deleteItem(key);
		}
	}

	@Override
	public long count() {
		ScanSpec spec = new ScanSpec().withSelect(Select.COUNT)
				.withSelect(Select.SPECIFIC_ATTRIBUTES)
				.withAttributesToGet(hashAttribute);
		ItemCollection<ScanOutcome> items = getTable().scan(spec);
		long count = 0;
		for (Page<Item, ScanOutcome> page : items.pages()) {
			count += page.size();
		}
		return count;
	}

	@Override
	public <S extends T> S save(S entity) {
		Item item = serialize(entity);
		getTable().putItem(item);
		return entity;
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		for (S entity : entities) {
			save(entity);
		}
		return entities;
	}

	@Override
	public boolean exists(I id) {
		return (null != findOne(id));
	}

	@Override
	public T findOne(I id) {
		PrimaryKey key = new PrimaryKey(hashAttribute, retrieveHashKey(id));
		if (null != rangeAttribute) {
			key.addComponent(rangeAttribute, retrieveRangeKey(id));
		}
		Item item = getTable().getItem(key);
		T entity = null;
		if (null != item) {
			entity = deserialize(item);
		}
		return entity;
	}

	@Override
	public Iterable<T> findAll() {
		ItemCollection<ScanOutcome> items = getTable().scan();
		Collection<T> results = new LinkedList<T>();
		for (Item item : items) {
			T entity = deserialize(item);
			results.add(entity);
		}
		return results;
	}

	@Override
	public List<T> findAll(Iterable<I> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(I id) {
		T entity = findOne(id);
		if (null != entity) {
			delete(entity);
		}
	}

	@Override
	public void delete(T entity) {
		PrimaryKey key = new PrimaryKey(hashAttribute, retrieveHashKey(entity));
		if (null != rangeAttribute) {
			key.addComponent(rangeAttribute, retrieveRangeKey(entity));
		}
		getTable().deleteItem(key);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}
}
