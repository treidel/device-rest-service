package com.fancypants.stream.kafka.config;

import java.net.InetAddress;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.stream.kafka.writer.KafkaStreamWriter;
import com.fancypants.stream.writer.StreamWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KafkaStreamConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(KafkaStreamConfig.class);

	private static final String KAFKA_BROKERS_ENVVAR = "KAFKA_BROKERS";
	private static final String KAFKA_TOPIC_ENVVAR = "KAFKA_TOPIC";

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public Producer<String, byte[]> producer(@Value("${" + KAFKA_BROKERS_ENVVAR
			+ "}") String brokers) throws Exception {
		LOG.trace("kafkaProducer enter");
		// create the producer config
		Properties properties = new Properties();
		properties.put("bootstrap.servers", brokers);
		properties.put("client.id", InetAddress.getLocalHost().getHostName());
		properties.put("value.serializer",
				"org.apache.kafka.common.serialization.ByteArraySerializer");
		properties.put("key.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		Producer<String, byte[]> producer = new KafkaProducer<>(properties);
		//
		LOG.trace("kafkaProducer exit {}", producer);
		return producer;
	}

	@Bean
	@Autowired
	public StreamWriter<RawRecordEntity> rawRecordStreamWriter(@Value("${"
			+ KAFKA_TOPIC_ENVVAR + "}") String topic,
			Producer<String, byte[]> producer) {
		LOG.trace("rawRecordStreamWriter enter {}={}", "producer", producer);
		// now create the writer
		StreamWriter<RawRecordEntity> streamWriter = new KafkaStreamWriter<RawRecordEntity>(
				objectMapper, producer, RawRecordEntity.class, topic);
		return streamWriter;
	}
}
