package com.fancypants.storm.kinesis.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import storm.trident.spout.ITridentSpout;
import storm.trident.spout.RichSpoutBatchExecutor;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.stormspout.InitialPositionInStream;
import com.amazonaws.services.kinesis.stormspout.KinesisSpout;
import com.amazonaws.services.kinesis.stormspout.KinesisSpoutConfig;
import com.fancypants.storm.kinesis.device.record.scheme.RawRecordScheme;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@Configuration
public class KinesisConfig {
	private final static String ZOOKEEPER_ENDPOINT = "localhost:2000";
	private final static String ZOOKEEPER_PREFIX = "kinesis_spout";

	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void init() {
		// setup date serialization
		objectMapper.setDateFormat(new ISO8601DateFormat());
	}

	@Bean
	public AmazonKinesis amazonKinesis() {
		AmazonKinesis amazonKinesis = new AmazonKinesisClient();
		return amazonKinesis;
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public ITridentSpout spout() {
		// fetch the stream name
		String streamName = System
				.getProperty("amazon.kinesis.stream.rawrecord");
		// setup the spout config
		final KinesisSpoutConfig config = new KinesisSpoutConfig(streamName,
				ZOOKEEPER_ENDPOINT)
				.withZookeeperPrefix(ZOOKEEPER_PREFIX)
				.withKinesisRecordScheme(new RawRecordScheme(objectMapper))
				.withInitialPositionInStream(
						InitialPositionInStream.TRIM_HORIZON);
		// create the spout
		final KinesisSpout spout = new KinesisSpout(config,
				new DefaultAWSCredentialsProviderChain(),
				new ClientConfiguration());
		// wrap it as a trident spout
		return new RichSpoutBatchExecutor(spout);
	} 
}
