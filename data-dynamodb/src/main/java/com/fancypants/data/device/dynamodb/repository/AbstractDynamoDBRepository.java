package com.fancypants.data.device.dynamodb.repository;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractDynamoDBRepository<E, I extends Serializable>
		implements CrudRepository<E, I>, Serializable {

	private static final long serialVersionUID = -3291793409692029613L;

	private static final Logger LOG = LoggerFactory.getLogger(AbstractDynamoDBRepository.class);

	private final Class<E> clazz;

	@Autowired
	private DynamoDB dynamoDB;

	@Autowired
	private ObjectMapper objectMapper;

	protected AbstractDynamoDBRepository(Class<E> clazz) {
		LOG.trace("AbstractDynamoDBRepository entry {}={}", "clazz", clazz);
		// store variables
		this.clazz = clazz;
		LOG.trace("AbstractDynamoDBRepository exit");
	}

	protected AbstractDynamoDBRepository(Class<E> clazz, ObjectMapper objectMapper, DynamoDB dynamoDB) {
		LOG.trace("AbstractDynamoDBRepository entry {}={}", "clazz", clazz, "objectMapper", objectMapper, "dynamoDB",
				dynamoDB);
		// store variables
		this.clazz = clazz;
		this.objectMapper = objectMapper;
		this.dynamoDB = dynamoDB;
		LOG.trace("AbstractDynamoDBRepository exit");
	}

	@PostConstruct
	private void init() {
		LOG.trace("init enter");

		// make sure the data is serializable
		Assert.isTrue(objectMapper.canSerialize(clazz));
		LOG.trace("init exit {}", dynamoDB);
	}

	protected Class<E> getEntityClass() {
		return clazz;
	}

	protected DynamoDB getDynamoDB() {
		return dynamoDB;
	}

	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	protected E deserialize(Item item) {
		String json = item.toJSON();
		try {
			E entity = objectMapper.readValue(json, clazz);
			return entity;
		} catch (IOException e) {
			LOG.error("invalid json {}", json);
			return null;
		}
	}

	protected Item serialize(E entity) {
		try {
			String json = objectMapper.writeValueAsString(entity);
			Item item = Item.fromJSON(json);
			return item;
		} catch (JsonProcessingException e) {
			LOG.error("can't serialize", e);
			return null;
		}
	}

	protected abstract PrimaryKey retrievePrimaryKey(E entity);

	protected abstract PrimaryKey retrievePrimaryKey(I id);

	protected abstract PrimaryKey retrievePrimaryKey(Item item);

}
