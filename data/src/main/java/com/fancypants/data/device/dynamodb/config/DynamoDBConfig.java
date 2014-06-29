package com.fancypants.data.device.dynamodb.config;

import java.io.Serializable;

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
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;
import com.fancypants.data.device.dynamodb.repository.impl.RecordRepositoryImpl;

@Configuration
@PropertySource("classpath:environment.properties")
public class DynamoDBConfig {	
	
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
		return new BasicAWSCredentials(getAmazonAWSAccessKey(), getAmazonAWSSecretKey());
	}
	
	@Bean
	public RecordRepository recordRepository() {
		DynamoDBEntityInformation<RecordEntity, RecordId> entityInformation = getEntityInformation(RecordEntity.class);
		return new RecordRepositoryImpl(dynamoDBOperations(), entityInformation);
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
	
	private <T, ID extends Serializable> DynamoDBEntityInformation<T, ID> getEntityInformation(final Class<T> domainClass) {

		DynamoDBEntityMetadataSupport<T, ID> metadata = new DynamoDBEntityMetadataSupport<T, ID>(domainClass);
		return metadata.getEntityInformation();
	}
	
}
