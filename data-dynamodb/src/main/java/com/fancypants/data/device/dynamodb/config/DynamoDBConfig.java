package com.fancypants.data.device.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;

@Configuration
@ComponentScan(basePackageClasses = { DataDeviceDynamoDBScanMe.class })
public class DynamoDBConfig {

	private static final String AMAZON_DYNAMODB_ENDPOINT_ENVVAR = "AWS_DYNAMODB_ENDPOINT";

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient();
		amazonDynamoDB.setEndpoint(getAmazonDynamoDBEndpoint());
		return amazonDynamoDB;
	}

	private String getAmazonDynamoDBEndpoint() {
		return ConfigUtils
				.retrieveEnvVarOrFail(AMAZON_DYNAMODB_ENDPOINT_ENVVAR);
	}

}
