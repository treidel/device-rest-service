package com.fancypants.test.data.dynamodb.config;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fancypants.common.CommonScanMe;
import com.fancypants.test.data.dynamodb.TestDynamoDBDataScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class,
		TestDynamoDBDataScanMe.class })
public class DynamoDBDataTestConfig {

	@Autowired
	private AWSCredentials awsCredentials;

	@Bean(name = "tablePrefix")
	public String tablePrefix() {
		return "test";
	}

	@Bean
	public DynamoDB dynamoDB() {
		// create the dynamodb client
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(awsCredentials);
		// create the wrapper
		return new DynamoDB(amazonDynamoDB);
	}

	@PostConstruct
	public void init() throws Exception {

		// delete the test tables in case they still exist
		deleteTable("test_devices");
		deleteTable("test_raw");
		deleteTable("test_hourly");

		// create the test tables
		createTable("test_devices", "device");
		createTable("test_raw", "device", "uuid");
		createTable("test_hourly", "device", "date");
	}

	@PreDestroy
	private void cleanup() throws Exception {
		// delete the test tables in case they still exist
		deleteTable("test_devices");
		deleteTable("test_raw");
		deleteTable("test_hourly");

	}

	private void createTable(String tableName, String hashKey) throws Exception {
		createTable(tableName, hashKey, null);
	}

	private void createTable(String tableName, String hashKey, String rangeKey)
			throws Exception {
		// setup the keys
		Collection<KeySchemaElement> keys = new ArrayList<KeySchemaElement>(2);
		Collection<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>(
				2);
		keys.add(new KeySchemaElement(hashKey, KeyType.HASH));
		attributes.add(new AttributeDefinition(hashKey, ScalarAttributeType.S));
		if (null != rangeKey) {
			keys.add(new KeySchemaElement(rangeKey, KeyType.RANGE));
			attributes.add(new AttributeDefinition(rangeKey,
					ScalarAttributeType.S));
		}
		// setup the capacity
		ProvisionedThroughput throughput = new ProvisionedThroughput(1L, 1L);
		// create the request
		CreateTableRequest request = new CreateTableRequest()
				.withTableName(tableName).withKeySchema(keys)
				.withAttributeDefinitions(attributes)
				.withProvisionedThroughput(throughput);
		// issue the request
		Table table = dynamoDB().createTable(request);
		table.waitForActive();
	}

	private void deleteTable(String tableName) throws Exception {
		try {
			Table table = dynamoDB().getTable(tableName);
			table.delete();
			table.waitForDelete();
		} catch (ResourceNotFoundException e) {
			// this is ok
		}
	}
}
