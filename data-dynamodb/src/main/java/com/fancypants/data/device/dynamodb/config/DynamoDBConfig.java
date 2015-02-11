package com.fancypants.data.device.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;

@Configuration
@ComponentScan(basePackageClasses = { DataDeviceDynamoDBScanMe.class })
public class DynamoDBConfig {

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient();
		amazonDynamoDB.setEndpoint(getAmazonDynamoDBEndpoint());
		return amazonDynamoDB;
	}

	private String getAmazonDynamoDBEndpoint() {
		return System.getProperty("amazon.dynamodb.endpoint");
	}

}
