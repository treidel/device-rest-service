package com.fancypants.data.device.dynamodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.data.device.dynamodb.credentials.SerializableCredentials;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class,
		DataDeviceDynamoDBScanMe.class })
public class DynamoDBConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(DynamoDBConfig.class);

	public static final String AMAZON_ACCESS_KEY_ID_ENVVAR = "AWS_ACCESS_KEY_ID";
	public static final String AMAZON_SECRET_ACCESS_KEY_ENVVAR = "AWS_SECRET_ACCESS_KEY";
	public static final String AMAZON_DYNAMODB_ENDPOINT_ENVVAR = "AWS_DYNAMODB_ENDPOINT";

	@Bean
	public SerializableCredentials serializableCredentials(
			@Value("${" + AMAZON_ACCESS_KEY_ID_ENVVAR + "}") String accessKeyId,
			@Value("${" + AMAZON_SECRET_ACCESS_KEY_ENVVAR + "}") String secretAccessKey) {
		LOG.trace("serializableCredentials enter");
		SerializableCredentials serializableCredentials = new SerializableCredentials(
				accessKeyId, secretAccessKey);
		LOG.trace("serializableCredentials exit {}", serializableCredentials);
		return serializableCredentials;
	}
}
