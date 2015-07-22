package com.fancypants.data.device.dynamodb.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.fancypants.data.device.dynamodb.config.DynamoDBConfig;
import com.fancypants.data.device.dynamodb.credentials.SerializableCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractDynamoDBRepository<T, I extends Serializable>
		implements CrudRepository<T, I>, Serializable {

	private static final long serialVersionUID = -3291793409692029613L;

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractDynamoDBRepository.class);

	private final Class<T> clazz;
	private final String hashAttribute;
	private final String rangeAttribute;

	private transient DynamoDB dynamoDB;

	@Autowired
	private SerializableCredentials awsCredentials;

	@Autowired
	@Qualifier(DynamoDBConfig.AMAZON_DYNAMODB_ENDPOINT_NAME)
	private String endpoint;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired(required = false)
	@Qualifier("tablePrefix")
	private String tablePrefix;

	protected AbstractDynamoDBRepository(Class<T> clazz, String hashAttribute) {
		this(clazz, hashAttribute, null);
	}

	protected AbstractDynamoDBRepository(Class<T> clazz, String hashAttribute,
			String rangeAttribute) {
		LOG.trace("AbstractDynamoDBRepository entry");

		// store variables
		this.clazz = clazz;
		this.hashAttribute = hashAttribute;
		this.rangeAttribute = rangeAttribute;
		LOG.trace("AbstractDynamoDBRepository entry");
	}

	@PostConstruct
	private void init() {
		LOG.trace("init enter");

		// make sure the data is serializable
		Assert.isTrue(objectMapper.canSerialize(clazz));

		// create the credential provider
		AWSCredentialsProvider awsCredentialsProvider = new StaticCredentialsProvider(
				awsCredentials);
		// create the client
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(
				awsCredentialsProvider);
		amazonDynamoDB.setEndpoint(endpoint);
		// now create the dynamoDB object
		dynamoDB = new DynamoDB(amazonDynamoDB);
		LOG.trace("init exit {}", dynamoDB);
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
		Item item = serialize((T) entity);
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
		PrimaryKey key = new PrimaryKey(hashAttribute,
				retrieveHashKey((T) entity));
		if (null != rangeAttribute) {
			key.addComponent(rangeAttribute, retrieveRangeKey((T) entity));
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
