package com.fancypants.test.data.dynamodb.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fancypants.common.CommonScanMe;
import com.fancypants.data.device.dynamodb.repository.DynamoDBDeviceRepository;
import com.fancypants.data.device.dynamodb.repository.DynamoDBHourlyRecordRepository;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.test.data.dynamodb.TestDynamoDBDataScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, TestDynamoDBDataScanMe.class })
@PropertySource("classpath:/test.properties")
public class DynamoDBDataTestConfig {

	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBDataTestConfig.class);

	@Autowired
	private DynamoDB dynamoDB;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PostConstruct
	public void init() throws Exception {

		LOG.trace("init enter");

		// cleanup first
		cleanup();

		// create the static tables
		{
			List<KeySchemaElement> deviceKeys = new ArrayList<>(1);
			deviceKeys.add(new KeySchemaElement(DeviceEntity.HASH_ATTRIBUTE, KeyType.HASH));
			List<AttributeDefinition> deviceAttributes = new ArrayList<>();
			deviceAttributes.add(new AttributeDefinition(DeviceEntity.HASH_ATTRIBUTE, ScalarAttributeType.S));
			Table deviceTable = dynamoDB.createTable(DynamoDBDeviceRepository.TABLE_NAME, deviceKeys, deviceAttributes,
					new ProvisionedThroughput(1L, 1L));
			deviceTable.waitForActive();
		}

		{
			List<KeySchemaElement> monthlyKeys = new ArrayList<>(1);
			monthlyKeys.add(new KeySchemaElement(EnergyConsumptionRecordEntity.HASH_ATTRIBUTE, KeyType.HASH));
			monthlyKeys.add(new KeySchemaElement(EnergyConsumptionRecordEntity.RANGE_ATTRIBUTE, KeyType.RANGE));
			List<AttributeDefinition> monthlyAttributes = new ArrayList<>();
			monthlyAttributes
					.add(new AttributeDefinition(EnergyConsumptionRecordEntity.HASH_ATTRIBUTE, ScalarAttributeType.S));
			monthlyAttributes
					.add(new AttributeDefinition(EnergyConsumptionRecordEntity.RANGE_ATTRIBUTE, ScalarAttributeType.S));
			Table monthlyTable = dynamoDB.createTable(DynamoDBHourlyRecordRepository.TABLE_NAME, monthlyKeys,
					monthlyAttributes, new ProvisionedThroughput(1L, 1L));
			monthlyTable.waitForActive();
		}

	}

	@PreDestroy
	private void cleanup() throws Exception {
		// query the list of tables
		TableCollection<ListTablesResult> tables = dynamoDB.listTables();
		// delete all existing tables
		for (Table table : tables) {
			LOG.info("removing old table: {}", table.getTableName());
			table.delete();
			table.waitForDelete();
		}
		LOG.trace("fini exit");
	}
}
