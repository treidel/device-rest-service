package com.fancypants.data.device.kinesis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.fancypants.data.device.kinesis.stream.StreamWriter;

@Configuration
public class KinesisConfig {


	@Autowired
	private AWSCredentials awsCredentials;

	@Bean
	public AmazonKinesis amazonKinesis() {
		AmazonKinesis amazonKinesis = new AmazonKinesisClient(awsCredentials);	
		return amazonKinesis;
	}

	@Bean
	public StreamWriter rawRecordStreamWriter() {
		String streamName = System.getProperty("amazon.kinesis.stream.rawrecord");
		StreamWriter streamWriter = new StreamWriter(amazonKinesis(), streamName);
		return streamWriter;
	}
	
}
