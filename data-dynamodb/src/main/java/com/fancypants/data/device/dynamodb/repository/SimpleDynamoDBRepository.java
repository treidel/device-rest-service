package com.fancypants.data.device.dynamodb.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.fancypants.data.device.dynamodb.credentials.SerializableCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SimpleDynamoDBRepository<E, I extends Serializable> extends AbstractDynamoDBRepository<E, I> {

	private static final long serialVersionUID = -3291793409692029613L;

	private static final Logger LOG = LoggerFactory.getLogger(SimpleDynamoDBRepository.class);

	protected SimpleDynamoDBRepository(Class<E> clazz) {
		super(clazz);
		LOG.trace("SimpleDynamoDBRepository enter {}={}", "clazz", clazz);
		LOG.trace("SimpleDynamoDBRepository exit");
	}

	SimpleDynamoDBRepository(Class<E> clazz, DynamoDB dynamoDB, SerializableCredentials awsCredentials,
			ObjectMapper objectMapper, String endpoint) {
		super(clazz, dynamoDB, awsCredentials, objectMapper, endpoint);
		LOG.trace("SimpleDynamoDBRepository enter {}={} {}={} {}={} {}={} {}={}", "clazz", clazz, "dynamoDB", dynamoDB,
				"awsCredentials", awsCredentials, "objectMapper", objectMapper, "endpoint", endpoint);
		LOG.trace("SimpleDynamoDBRepository exit");

	}

	protected Table getTable() {
		// get the base table name
		String baseTableName = retrieveTableName();
		return getDynamoDB().getTable(baseTableName);
	}

	protected abstract String retrieveTableName();

	@Override
	public void deleteAll() {
		LOG.trace("deleteAll enter");
		Table table = getTable();
		ItemCollection<ScanOutcome> items = table.scan();
		for (Item item : items) {
			PrimaryKey key = retrievePrimaryKey(item);
			table.deleteItem(key);
		}
		LOG.trace("deleteAll exit");
	}

	@Override
	public long count() {
		LOG.trace("count enter");
		ScanSpec spec = new ScanSpec().withSelect(Select.COUNT);
		ItemCollection<ScanOutcome> items = getTable().scan(spec);
		Page<Item, ScanOutcome> page = items.firstPage();
		while (true == page.hasNextPage()) {
			page.nextPage();
		}
		long count = items.getTotalScannedCount();
		LOG.trace("count exit", count);
		return count;
	}

	@Override
	public <S extends E> S save(S entity) {
		LOG.trace("save enter", "entity", entity);
		Item item = serialize((E) entity);
		getTable().putItem(item);
		LOG.trace("save exit", "entity", entity);
		return entity;
	}

	@Override
	public <S extends E> Iterable<S> save(Iterable<S> entities) {
		LOG.trace("save enter", "entities", entities);
		for (S entity : entities) {
			save(entity);
		}
		LOG.trace("save exit", "entities", entities);
		return entities;
	}

	@Override
	public boolean exists(I id) {
		LOG.trace("exists enter", "id", id);
		boolean found = (null != findOne(id));
		LOG.trace("exists exit", "found", found);
		return found;
	}

	@Override
	public E findOne(I id) {
		LOG.trace("findOne enter", "id", id);
		PrimaryKey key = retrievePrimaryKey(id);
		Item item = getTable().getItem(key);
		E entity = null;
		if (null != item) {
			entity = deserialize(item);
		}
		LOG.trace("findOne exit", "entity", entity);
		return entity;
	}

	@Override
	public Iterable<E> findAll() {
		LOG.trace("findAll enter");
		ItemCollection<ScanOutcome> items = getTable().scan();
		Collection<E> results = new LinkedList<E>();
		for (Item item : items) {
			E entity = deserialize(item);
			results.add(entity);
		}
		LOG.trace("findAll exit", "results", results);
		return results;
	}

	@Override
	public List<E> findAll(Iterable<I> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(I id) {
		LOG.trace("delete enter", "id", id);
		E entity = findOne(id);
		if (null != entity) {
			delete(entity);
		}
		LOG.trace("delete exit");
	}

	@Override
	public void delete(E entity) {
		LOG.trace("delete enter", "entity", entity);
		PrimaryKey key = retrievePrimaryKey(entity);
		getTable().deleteItem(key);
		LOG.trace("delete exit");
	}

	@Override
	public void delete(Iterable<? extends E> entities) {
		LOG.trace("delete enter", "entities", entities);
		for (E entity : entities) {
			delete(entity);
		}
		LOG.trace("delete exit");
	}
}
