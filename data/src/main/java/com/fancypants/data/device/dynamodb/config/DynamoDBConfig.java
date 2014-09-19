package com.fancypants.data.device.dynamodb.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = DataDeviceDynamoDBScanMe.class, dynamoDBOperationsRef = "dynamoDBOperations")
public class DynamoDBConfig {

	public static final String ISO8601_DATEFORMAT_BEAN = "iso8601DateFormat";
	
	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(getAmazonAWSDynamoDBCredentials());
		amazonDynamoDB.setEndpoint(getAmazonDynamoDBEndpoint());
		return amazonDynamoDB;
	}

	@Bean
	public DynamoDBOperations dynamoDBOperations() {
		return new DynamoDBTemplate(amazonDynamoDB());
	}

	@Bean
	public DateFormat iso8601DateFormat() {
		// use ISO8601/RFC3339 time format
		//TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		//df.setTimeZone(tz);
		return df;
	}

	private AWSCredentials getAmazonAWSDynamoDBCredentials() {
		return new BasicAWSCredentials(getAmazonAWSAccessKey(),
				getAmazonAWSSecretKey());
	}

	private String getAmazonAWSAccessKey() {
		return System.getProperty("amazon.aws.accesskey");
	}

	private String getAmazonAWSSecretKey() {
		return System.getProperty("amazon.aws.secretkey");
	}

	
	private String getAmazonDynamoDBEndpoint() {
		return System.getProperty("amazon.dynamodb.endpoint");
	}

}
