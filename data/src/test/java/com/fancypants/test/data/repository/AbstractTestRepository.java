package com.fancypants.test.data.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.ReflectionEntityInformation;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractTestRepository<T, I extends Serializable>
		implements CrudRepository<T, I>, Serializable {

	private static final long serialVersionUID = 5088964975582070882L;
	private static final Map<Class<?>, Map<?, ?>> SINGLETON = new HashMap<Class<?>, Map<?, ?>>();

	private final Map<I, T> table;
	private final Class<T> clazz;
	private transient ReflectionEntityInformation<T, I> entityInformation;

	@Autowired
	private ObjectMapper mapper;

	@PostConstruct
	private void init() {
		entityInformation = new ReflectionEntityInformation<>(clazz);
	}

	@SuppressWarnings("unchecked")
	protected AbstractTestRepository(Class<T> clazz) {
		// store the params
		this.clazz = clazz;
		// create the map if needed
		synchronized (SINGLETON) {
			Map<I, T> table = (Map<I, T>) SINGLETON.get(clazz);
			if (null == table) {
				table = new HashMap<I, T>();
				SINGLETON.put(clazz, table);
			}
			this.table = table;
		}
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
		I id = entityInformation.getId(entity);
		try {
			String json = mapper.writeValueAsString(entity);
			T copy = mapper.readValue(json.getBytes(), clazz);
			table.put(id, copy);
			return entity;
		} catch (IOException e) {
			throw new IllegalStateException(e);
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
		I id = entityInformation.getId(entity);
		delete(id);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}
}
