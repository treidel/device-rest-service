package com.fancypants.data.device.dynamodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class, DataDeviceDynamoDBScanMe.class })
public class DynamoDBConfig {

	private static final Logger LOG = LoggerFactory.getLogger(DynamoDBConfig.class);

	private static final String AMAZON_ACCESS_KEY_ID_ENVVAR = "AWS_ACCESS_KEY_ID";
	private static final String AMAZON_SECRET_ACCESS_KEY_ENVVAR = "AWS_SECRET_ACCESS_KEY";
	private static final String AMAZON_DYNAMODB_ENDPOINT_ENVVAR = "AWS_DYNAMODB_ENDPOINT";

	@Bean
	public DynamoDB dynamoDB(@Value("${" + AMAZON_ACCESS_KEY_ID_ENVVAR + "}") String accessKeyId,
			@Value("${" + AMAZON_SECRET_ACCESS_KEY_ENVVAR + "}") String secretAccessKey,
			@Value("${" + AMAZON_DYNAMODB_ENDPOINT_ENVVAR + "}") String endpoint) {
		LOG.trace("dynamoDB enter {}={} {}={} {}={}", "accessKeyId", accessKeyId, "secretAccessKey", secretAccessKey,
				"endpoint", endpoint);
		AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(credentials);
		amazonDynamoDB.setEndpoint(endpoint);
		DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
		LOG.trace("dynamoDB exit {}", dynamoDB);
		return dynamoDB;
	}
}
