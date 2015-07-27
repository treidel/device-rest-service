package com.fancypants.test.data.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleTestRepository<T, I extends Serializable> extends
		AbstractTestRepository<T, I> {

	private static final long serialVersionUID = 5088964975582070882L;

	private final Map<I, T> table;

	@SuppressWarnings("unchecked")
	protected SimpleTestRepository(Class<T> clazz, String tableName) {
		super(clazz);
		// retrieve the table
		this.table = (Map<I, T>) AbstractTestRepository.findOrCreateTable(
				tableName, (Class<? extends Map<?, ?>>) HashMap.class);
	}

	@Override
	public void deleteAll() {
		table.clear();
	}

	@Override
	public long count() {
		return table.size();
	}

	@Override
	public <S extends T> S save(S entity) {
		I id = getEntityInformation().getId(entity);
		T copy = clone(entity);
		table.put(id, copy);
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
		return table.containsKey(id);
	}

	@Override
	public T findOne(I id) {
		return table.get(id);
	}

	@Override
	public Iterable<T> findAll() {
		return table.values();
	}

	@Override
	public List<T> findAll(Iterable<I> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(I id) {
		table.remove(id);
	}

	@Override
	public void delete(T entity) {
		I id = getEntityInformation().getId(entity);
		delete(id);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}
}
