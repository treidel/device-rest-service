package com.fancypants.data.device.dynamodb.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.spec.ListTablesSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fancypants.data.partitioner.Partition;
import com.fancypants.data.partitioner.Partitioner;
import com.fancypants.data.repository.PartitionedRepository;

public abstract class PartitionedDynamoDBRepository<E, I extends Serializable, T>
		extends AbstractDynamoDBRepository<E, I>implements PartitionedRepository<E, I> {

	private static final long serialVersionUID = -3291793409692029613L;

	private static final Logger LOG = LoggerFactory.getLogger(PartitionedDynamoDBRepository.class);

	private final Partitioner<E, T> partitioner;
	private final String tablePrefix;

	protected PartitionedDynamoDBRepository(Class<E> clazz, Partitioner<E, T> partitioner) {
		super(clazz);
		LOG.trace("PartitionedDynamoDBRepository enter", "clazz", clazz);
		// store the partitioner
		this.partitioner = partitioner;
		// compute the table prefix
		this.tablePrefix = retrieveBaseTableName() + "_";
		LOG.trace("PartitionedDynamoDBRepository exit");
	}

	protected Table getTable(E entity) {
		LOG.trace("getTable enter", "entity", entity);
		// run the partitioner
		Partition partition = partitioner.partitionByEntity(entity);
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
	public void createPartition(Partition partition) {
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
			Collection<AttributeDefinition> attributes = new ArrayList<>(elements.size());
			for (KeySchemaElement element : elements) {
				attributes.add(new AttributeDefinition(element.getAttributeName(), ScalarAttributeType.S));
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
	public void deletePartition(Partition partition) {
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
	public List<Partition> listPartitions() {
		LOG.trace("listPartitions enter");
		// query for a list of tables starting with our table base name
		TableCollection<ListTablesResult> tables = getDynamoDB()
				.listTables(new ListTablesSpec().withExclusiveStartTableName(retrieveBaseTableName()));
		// create a response list
		List<Partition> partitions = new LinkedList<>();
		for (Table table : tables) {
			// give up if we get to tables that aren't partitions of this
			// logical table
			if (false == table.getTableName().startsWith(tablePrefix)) {
				break;
			}
			// the partition name is the portion of the table name after the
			// prefix with the dots swapped back to dashes
			String partitionName = table.getTableName().substring(tablePrefix.length()).replace('.', '-');
			Partition partition = new Partition(partitionName);
			// add it to the list
			partitions.add(partition);
		}

		LOG.trace("listPartitions exit {}", partitions);
		return partitions;
	}

	@Override
	public CrudRepository<E, I> retrievePartitionTable(Partition partition) {
		LOG.trace("retrievePartitionTable enter {}={}", "partition", partition);
		// create the wrapper
		PartitionWrapper wrapper = new PartitionWrapper(partition);
		LOG.trace("partition exit", wrapper);
		return wrapper;
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		LOG.trace("count enter");
		// get a list of partitions
		List<Partition> partitions = listPartitions();
		long value = 0;
		// iterate through each partition
		for (Partition partition : partitions) {
			// get the partition table
			CrudRepository<?, ?> partitionTable = retrievePartitionTable(partition);
			if (null != partitionTable) {
				// query for counts
				value += partitionTable.count();
			}
		}
		LOG.trace("count exit {}", value);
		return value;
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

	private String computeTableName(Partition partition) {
		// substitute dots for colons to ensure a legal table name
		return this.tablePrefix + partition.getValue().replace(':', '.');
	}

	private class PartitionWrapper extends SimpleDynamoDBRepository<E, I> {

		private static final long serialVersionUID = 6415591338541281719L;

		private final Partition partition;

		public PartitionWrapper(Partition partition) {
			super(PartitionedDynamoDBRepository.this.getEntityClass(),
					PartitionedDynamoDBRepository.this.getObjectMapper(),
					PartitionedDynamoDBRepository.this.getDynamoDB());
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
			LOG.trace("PartitionWrapper.retrievePrimaryKey enter", "entity", entity);
			PrimaryKey primaryKey = PartitionedDynamoDBRepository.this.retrievePrimaryKey(entity);
			LOG.trace("PartitionWrapper.retrievePrimaryKey exit", primaryKey);
			return primaryKey;
		}

		@Override
		protected PrimaryKey retrievePrimaryKey(I id) {
			LOG.trace("PartitionWrapper.retrievePrimaryKey enter", "id", id);
			PrimaryKey primaryKey = PartitionedDynamoDBRepository.this.retrievePrimaryKey(id);
			LOG.trace("PartitionWrapper.retrievePrimaryKey exit", primaryKey);
			return primaryKey;
		}

		@Override
		protected PrimaryKey retrievePrimaryKey(Item item) {
			LOG.trace("PartitionWrapper.retrievePrimaryKey enter", "item", item);
			PrimaryKey primaryKey = PartitionedDynamoDBRepository.this.retrievePrimaryKey(item);
			LOG.trace("PartitionWrapper.retrievePrimaryKey exit", primaryKey);
			return primaryKey;
		}

	}
}
