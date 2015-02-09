package com.fancypants.test.data.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.ReflectionEntityInformation;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractTestRepository<I extends Serializable, T>
		implements CrudRepository<T, I> {

	private final ObjectMapper mapper;
	private final ReflectionEntityInformation<T, I> entityInfo;
	private final Map<I, T> table = new HashMap<I, T>();

	protected AbstractTestRepository(ObjectMapper mapper, Class<T> clazz) {
		this.mapper = mapper;
		this.entityInfo = new ReflectionEntityInformation<>(clazz);
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
		try {
			I id = entityInfo.getId(entity);
			String json = mapper.writeValueAsString(entity);
			Class<T> clazz = entityInfo.getJavaType();
			T copy = mapper.readValue(json.getBytes(), clazz);
			table.put(id, copy);
			return entity;
		} catch (IOException e) {
			throw new IllegalAccessError();
		}
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
		I id = entityInfo.getId(entity);
		delete(id);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}
}
