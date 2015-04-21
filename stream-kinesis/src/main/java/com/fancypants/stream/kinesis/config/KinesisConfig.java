package com.fancypants.stream.kinesis.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.stream.kinesis.writer.KinesisStreamWriter;
import com.fancypants.stream.writer.StreamWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@Configuration
public class KinesisConfig {

	private static final String AMAZON_KINESIS_STREAM_ENVVAR = "AWS_KINESIS_STREAM";

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

	@Bean
	@Autowired
	public StreamWriter<RawRecordEntity> rawRecordStreamWriter(
			AmazonKinesis amazonKinesis) {
		KinesisStreamWriter<RawRecordEntity> streamWriter = new KinesisStreamWriter<RawRecordEntity>(
				objectMapper, amazonKinesis, RawRecordEntity.class,
				getStreamName());
		return streamWriter;
	}

	private String getStreamName() {
		return ConfigUtils.retrieveEnvVarOrFail(AMAZON_KINESIS_STREAM_ENVVAR);
	}
}
