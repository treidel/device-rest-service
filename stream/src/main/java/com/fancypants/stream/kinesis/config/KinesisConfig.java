package com.fancypants.stream.kinesis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.fancypants.stream.kinesis.entity.RawRecord;
import com.fancypants.stream.kinesis.writer.StreamWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KinesisConfig {

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public AmazonKinesis amazonKinesis() {
		AmazonKinesis amazonKinesis = new AmazonKinesisClient(
				getAmazonAWSKinesisCredentials());
		return amazonKinesis;
	}

	@Bean
	public StreamWriter<RawRecord> rawRecordStreamWriter() {
		String streamName = System
				.getProperty("amazon.kinesis.stream.rawrecord");
		StreamWriter<RawRecord> streamWriter = new StreamWriter<RawRecord>(
				objectMapper, amazonKinesis(), streamName);
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
