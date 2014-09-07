package com.fancypants.data.device.kinesis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.fancypants.data.device.kinesis.entity.RawRecord;
import com.fancypants.data.device.kinesis.stream.StreamWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KinesisConfig {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AWSCredentials awsCredentials;

	@Bean
	public AmazonKinesis amazonKinesis() {
		AmazonKinesis amazonKinesis = new AmazonKinesisClient(awsCredentials);
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

}
