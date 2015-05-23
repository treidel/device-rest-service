package com.fancypants.data.device.dynamodb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.fancypants.common.CommonScanMe;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.data.device.dynamodb.credentials.SerializableCredentials;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataDeviceDynamoDBScanMe.class })
public class DynamoDBConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(DynamoDBConfig.class);

	private static final String AMAZON_DYNAMODB_ENDPOINT_ENVVAR = "AWS_DYNAMODB_ENDPOINT";

	@Bean
	public SerializableCredentials serializableCredentials() {
		LOG.trace("serializableCredentials enter");
		// get the credentials from the environment
		AWSCredentials credentials = new DefaultAWSCredentialsProviderChain()
				.getCredentials();
		SerializableCredentials serializableCredentials = new SerializableCredentials(
				credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey());
		LOG.trace("serializableCredentials exit {}", serializableCredentials);
		return serializableCredentials;
	}

	@Bean
	public String amazonDynamoDBEndpoint() {
		return ConfigUtils
				.retrieveEnvVarOrFail(AMAZON_DYNAMODB_ENDPOINT_ENVVAR);
	}

}
