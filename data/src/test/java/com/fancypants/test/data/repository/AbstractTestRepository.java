package com.fancypants.test.data.repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.ReflectionEntityInformation;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractTestRepository<E, I extends Serializable>
		implements CrudRepository<E, I>, Serializable {

	private static final long serialVersionUID = 5088964975582070882L;
	private static final Map<String, Map<?, ?>> SINGLETON = new HashMap<String, Map<?, ?>>();

	private final Class<E> clazz;
	private transient ReflectionEntityInformation<E, I> entityInformation;

	@Autowired
	private ObjectMapper objectMapper;

	protected static Map<?, ?> findOrCreateTable(String tableName,
			Class<? extends Map<?, ?>> tableClazz) {
		try {
			synchronized (SINGLETON) {
				Map<?, ?> table = SINGLETON.get(tableName);
				if (null == table) {
					table = tableClazz.newInstance();
					SINGLETON.put(tableName, table);
				}
				return table;
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected static void deleteTable(String tableName) {
		synchronized (SINGLETON) {
			SINGLETON.remove(tableName);
		}
	}

	protected AbstractTestRepository(Class<E> clazz) {
		// store the params
		this.clazz = clazz;
	}

	@PostConstruct
	void init() {
		entityInformation = new ReflectionEntityInformation<>(clazz);
	}

	protected Class<E> getEntityClass() {
		return clazz;
	}

	protected ReflectionEntityInformation<E, I> getEntityInformation() {
		return entityInformation;
	}

	protected E clone(E entity) {
		try {
			String json = objectMapper.writeValueAsString(entity);
			E copy = objectMapper.readValue(json.getBytes(), clazz);
			return copy;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
