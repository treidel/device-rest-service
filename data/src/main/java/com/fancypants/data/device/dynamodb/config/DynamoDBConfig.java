package com.fancypants.data.device.dynamodb.config;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityInformation;
import org.socialsignin.spring.data.dynamodb.repository.support.DynamoDBEntityMetadataSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;
import com.fancypants.data.device.dynamodb.repository.impl.DeviceRepositoryImpl;
import com.fancypants.data.device.dynamodb.repository.impl.RecordRepositoryImpl;

@Configuration
@PropertySource("classpath:environment.properties")
public class DynamoDBConfig {

	public static final String ISO8601_DATEFORMAT_BEAN = "iso8601DateFormat";
	
	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(
				amazonAWSCredentials());
		amazonDynamoDB.setEndpoint(getAmazonDynamoDBEndpoint());
		return amazonDynamoDB;
	}

	@Bean
	public DynamoDBOperations dynamoDBOperations() {
		return new DynamoDBTemplate(amazonDynamoDB());
	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(getAmazonAWSAccessKey(),
				getAmazonAWSSecretKey());
	}

	@Bean
	public RecordRepository recordRepository() {
		DynamoDBEntityInformation<RecordEntity, RecordId> entityInformation = getEntityInformation(RecordEntity.class);
		return new RecordRepositoryImpl(dynamoDBOperations(), entityInformation);
	}

	@Bean
	public DeviceRepository deviceRepository() {
		DynamoDBEntityInformation<DeviceEntity, String> entityInformation = getEntityInformation(DeviceEntity.class);
		return new DeviceRepositoryImpl(dynamoDBOperations(), entityInformation);
	}
	
	@Bean
	DateFormat iso8601DateFormat() {
		// use ISO8601/RFC3339 time format
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		return df;
	}

	private String getAmazonDynamoDBEndpoint() {
		return System.getProperty("amazon.dynamodb.endpoint");
	}

	private String getAmazonAWSAccessKey() {
		return System.getProperty("amazon.aws.accesskey");
	}

	private String getAmazonAWSSecretKey() {
		return System.getProperty("amazon.aws.secretkey");
	}

	private <T, ID extends Serializable> DynamoDBEntityInformation<T, ID> getEntityInformation(
			final Class<T> domainClass) {

		DynamoDBEntityMetadataSupport<T, ID> metadata = new DynamoDBEntityMetadataSupport<T, ID>(
				domainClass);
		return metadata.getEntityInformation();
	}

}
