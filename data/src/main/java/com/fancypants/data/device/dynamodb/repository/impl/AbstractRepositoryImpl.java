package com.fancypants.data.device.dynamodb.repository.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;

public abstract class AbstractRepositoryImpl<T, I extends Serializable> implements CrudRepository<T, I> {

	private final DynamoDBMapper dynamoDBMapper;
	private final Class<T> clazz;

	@Autowired
	public AbstractRepositoryImpl(DynamoDBMapper dynamoDBMapper, Class<T> clazz) {
		this.dynamoDBMapper = dynamoDBMapper;
		this.clazz = clazz;
	}

	protected DynamoDBMapper getDynamoDBMapper() {
		return dynamoDBMapper;
	}
	
	@Override
	public void deleteAll() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		List<PowerConsumptionRecordEntity> records = dynamoDBMapper.scan(
				PowerConsumptionRecordEntity.class, expression);
		dynamoDBMapper.batchDelete(records);
	}

	@Override
	public long count() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		return dynamoDBMapper.count(PowerConsumptionRecordEntity.class,
				expression);
	}

	@Override
	public <S extends T> S save(S entity) {
		dynamoDBMapper.save(entity);
		return entity;
	}

	@Override
	public <S extends T> Iterable<S> save(
			Iterable<S> entities) {
		for (S entity : entities) {
			dynamoDBMapper.save(entity);
		}
		return entities;
	}

	@Override
	public boolean exists(I id) {
		return (null != findOne(id));
	}

	@Override
	public Iterable<T> findAll() {
		DynamoDBScanExpression expression = new DynamoDBScanExpression();
		List<T> records = dynamoDBMapper.scan(
				clazz, expression);
		return records;
	}

	@Override
	public List<T> findAll(Iterable<I> ids) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void delete(I id) {
		T entity = findOne(id);
		dynamoDBMapper.delete(entity);
	}

	@Override
	public void delete(T entity) {
		dynamoDBMapper.delete(entity);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		List<T> listToDelete = new LinkedList<T>();
		for (T entity : entities) {
			listToDelete.add(entity);
		}
		dynamoDBMapper.batchDelete(listToDelete);
	}
}
