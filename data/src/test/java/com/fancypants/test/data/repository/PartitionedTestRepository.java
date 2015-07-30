package com.fancypants.test.data.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

import com.fancypants.data.partitioner.Partition;
import com.fancypants.data.partitioner.Partitioner;
import com.fancypants.data.repository.PartitionedRepository;

public abstract class PartitionedTestRepository<E, I extends Serializable, T> extends AbstractTestRepository<E, I>
		implements PartitionedRepository<E, I> {

	private static final long serialVersionUID = 5088964975582070882L;

	@Autowired
	private ApplicationContext applicationContext;

	private final Partitioner<E, T> partitioner;

	protected PartitionedTestRepository(Class<E> clazz, Partitioner<E, T> partitioner) {
		super(clazz);
		// store the partitioner
		this.partitioner = partitioner;
	}

	protected Partitioner<E, T> getPartitioner() {
		return partitioner;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createPartition(Partition partition) {
		String tableName = computeTableName(partition);
		findOrCreateTable(tableName, ((Class<? extends Map<?, ?>>) HashMap.class));
	}

	@Override
	public void deletePartition(Partition partition) {
		String tableName = computeTableName(partition);
		deleteTable(tableName);
	}

	@Override
	public CrudRepository<E, I> retrievePartitionTable(Partition partition) {
		// create the wrapper
		PartitionWrapper wrapper = new PartitionWrapper(partition);
		// pump it up as a bean
		applicationContext.getAutowireCapableBeanFactory().autowireBean(wrapper);
		applicationContext.getAutowireCapableBeanFactory().initializeBean(wrapper, partition.getValue());
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
		// map the entity to a partition
		Partition partition = partitioner.partitionByEntity(entity);
		// get the partition table
		CrudRepository<E, I> repository = retrievePartitionTable(partition);
		repository.save(entity);
		return entity;
	}

	@Override
	public <S extends E> Iterable<S> save(Iterable<S> entities) {
		for (S entity : entities) {
			save(entity);
		}
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
		// map the entity to a partition
		Partition partition = partitioner.partitionByEntity(entity);
		// get the partition table
		CrudRepository<E, I> repository = retrievePartitionTable(partition);
		// get the id
		I id = getEntityInformation().getId(entity);
		// delete from the partition table
		repository.delete(id);
	}

	@Override
	public void delete(Iterable<? extends E> entities) {
		for (E entity : entities) {
			delete(entity);
		}
	}

	private String computeTableName(Partition partition) {
		return getEntityClass() + "_" + partition.getValue();
	}

	private class PartitionWrapper extends SimpleTestRepository<E, I> {

		private static final long serialVersionUID = 6840652155263395976L;

		protected PartitionWrapper(Partition partition) {
			super(PartitionedTestRepository.this.getEntityClass(), computeTableName(partition));
		}

	}
}
