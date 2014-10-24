package com.fancypants.stream.kinesis.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.stream.kinesis.writer.KinesisStreamWriter;
import com.fancypants.stream.writer.StreamWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@Configuration
public class KinesisConfig {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@PostConstruct
	public void init() {
		// setup date serialization
		objectMapper.setDateFormat(new ISO8601DateFormat());
	}

	@Bean
	public AmazonKinesis amazonKinesis() {
		AmazonKinesis amazonKinesis = new AmazonKinesisClient(
				getAmazonAWSKinesisCredentials());
		return amazonKinesis;
	}

	@Bean
	public StreamWriter<RawRecordEntity> rawRecordStreamWriter() {
		String streamName = System
				.getProperty("amazon.kinesis.stream.rawrecord");
		KinesisStreamWriter<RawRecordEntity> streamWriter = new KinesisStreamWriter<RawRecordEntity>(
				objectMapper, amazonKinesis(), RawRecordEntity.class,
				streamName);
		return streamWriter;
	}

	private AWSCredentials getAmazonAWSKinesisCredentials() {
		return new BasicAWSCredentials(getAmazonAWSAccessKey(),
				getAmazonAWSSecretKey());
	}

	private String getAmazonAWSAccessKey() {
		return System.getProperty("amazon.aws.accesskey");
	}

	private String getAmazonAWSSecretKey() {
		return System.getProperty("amazon.aws.secretkey");
	}

}
