package com.fancypants.data.device.dynamodb.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fancypants.data.partitioner.Partitioner;
import com.fancypants.data.repository.PartitionedRepository;

public abstract class PartitionedDynamoDBRepository<E, I extends Serializable>
		extends AbstractDynamoDBRepository<E, I> implements
		PartitionedRepository<E, I> {

	private static final long serialVersionUID = -3291793409692029613L;

	private static final Logger LOG = LoggerFactory
			.getLogger(PartitionedDynamoDBRepository.class);

	private final Partitioner<E> partitioner;

	@Autowired
	private ApplicationContext applicationContext;

	protected PartitionedDynamoDBRepository(Class<E> clazz,
			Partitioner<E> partitioner) {
		super(clazz);
		LOG.trace("PartitionedDynamoDBRepository enter", "clazz", clazz);
		this.partitioner = partitioner;
		LOG.trace("PartitionedDynamoDBRepository exit");
	}

	protected Table getTable(E entity) {
		LOG.trace("getTable enter", "entity", entity);
		// run the partitioner
		String partition = partitioner.partition(entity);
		// create the table name
		String tableName = computeTableName(partition);
		// fetch the table
		Table table = getDynamoDB().getTable(tableName);
		LOG.trace("getTable exit", table);
		return table;
	}

	protected abstract String retrieveBaseTableName();

	protected abstract Collection<KeySchemaElement> retrieveKeySchema();

	@Override
	public void createPartition(String partition) {
		LOG.trace("createPartition enter", "partition", partition);
		// compute the table name
		String tableName = computeTableName(partition);
		// see if the table exists
		Table table = getDynamoDB().getTable(tableName);
		try {
			table.describe();
		} catch (ResourceNotFoundException e) {
			// the table doesn't exist we create it
			LOG.debug("table not found, creating", tableName);
			// get the key schema from the child class
			Collection<KeySchemaElement> elements = retrieveKeySchema();
			Collection<AttributeDefinition> attributes = new ArrayList<>(
					elements.size());
			for (KeySchemaElement element : elements) {
				attributes.add(new AttributeDefinition(element
						.getAttributeName(), ScalarAttributeType.S));
			}
			// create + populate the API request
			CreateTableRequest request = new CreateTableRequest();
			request.setTableName(tableName);
			request.setKeySchema(elements);
			request.setAttributeDefinitions(attributes);
			request.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
			// trigger table creation
			table = getDynamoDB().createTable(request);
		}
		// wait for the table to become active
		try {
			table.waitForActive();
		} catch (InterruptedException e) {
			LOG.error("Interrupted while waiting for table to become active", e);
		}
		LOG.trace("createPartition exit");
	}

	@Override
	public void deletePartition(String partition) {
		LOG.trace("deletePartition enter", "partition", partition);
		// compute the table name
		String tableName = computeTableName(partition);
		// delete the table
		Table table = getDynamoDB().getTable(tableName);
		try {
			// query for the table info
			table.describe();
			// delete the table
			table.delete();
			// wait for the deletion to complete
			table.waitForDelete();
		} catch (ResourceNotFoundException e) {
			LOG.debug("table not found, not deleting", tableName);
		} catch (InterruptedException e) {
			LOG.error("Interrupted while waiting for table to delete", e);
		}
		LOG.trace("deletePartition exit");
	}

	@Override
	public CrudRepository<E, I> retrievePartitionTable(String partition) {
		LOG.trace("partition enter", "partition", partition);
		// create the wrapper
		PartitionWrapper wrapper = new PartitionWrapper(partition);
		applicationContext.getAutowireCapableBeanFactory()
				.autowireBean(wrapper);
		applicationContext.getAutowireCapableBeanFactory().initializeBean(
				wrapper, wrapper.retrieveTableName());
		LOG.trace("partition exit", wrapper);
		return wrapper;
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends E> S save(S entity) {
		LOG.trace("save enter", "entity", entity);
		Item item = serialize((E) entity);
		Table table = getTable(entity);
		table.putItem(item);
		LOG.trace("save exit", entity);
		return entity;
	}

	@Override
	public <S extends E> Iterable<S> save(Iterable<S> entities) {
		LOG.trace("save enter", "entities", entities);
		for (S entity : entities) {
			save(entity);
		}
		LOG.trace("save exit", entities);
		return entities;
	}

	@Override
	public boolean exists(I id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E findOne(I id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<E> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<E> findAll(Iterable<I> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(I id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(E entity) {
		LOG.trace("delete enter", "entity", entity);
		PrimaryKey key = retrievePrimaryKey(entity);
		Table table = getTable(entity);
		table.deleteItem(key);
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

	private String computeTableName(String partition) {
		// substitute dots for colons to ensure a legal table name
		return retrieveBaseTableName() + "_" + partition.replace(':', '.');
	}

	private class PartitionWrapper extends SimpleDynamoDBRepository<E, I> {

		private static final long serialVersionUID = 6415591338541281719L;

		private final String partition;

		public PartitionWrapper(String partition) {
			super(PartitionedDynamoDBRepository.this.getEntityClass());
			LOG.trace("PartitionWrapper enter", "partition", partition);
			this.partition = partition;
			LOG.trace("PartitionWrapper exit");
		}

		@Override
		protected String retrieveTableName() {
			LOG.trace("PartitionWrapper.retrieveTableName enter");
			// compute the table name
			String tableName = computeTableName(partition);
			LOG.trace("retrieveTableName exit", tableName);
			return tableName;
		}

		@Override
		protected PrimaryKey retrievePrimaryKey(E entity) {
			LOG.trace("PartitionWrapper.retrievePrimaryKey enter", "entity",
					entity);
			PrimaryKey primaryKey = PartitionedDynamoDBRepository.this
					.retrievePrimaryKey(entity);
			LOG.trace("PartitionWrapper.retrievePrimaryKey exit", primaryKey);
			return primaryKey;
		}

		@Override
		protected PrimaryKey retrievePrimaryKey(I id) {
			LOG.trace("PartitionWrapper.retrievePrimaryKey enter", "id", id);
			PrimaryKey primaryKey = PartitionedDynamoDBRepository.this
					.retrievePrimaryKey(id);
			LOG.trace("PartitionWrapper.retrievePrimaryKey exit", primaryKey);
			return primaryKey;
		}

		@Override
		protected PrimaryKey retrievePrimaryKey(Item item) {
			LOG.trace("PartitionWrapper.retrievePrimaryKey enter", "item", item);
			PrimaryKey primaryKey = PartitionedDynamoDBRepository.this
					.retrievePrimaryKey(item);
			LOG.trace("PartitionWrapper.retrievePrimaryKey exit", primaryKey);
			return primaryKey;
		}

	}
}
