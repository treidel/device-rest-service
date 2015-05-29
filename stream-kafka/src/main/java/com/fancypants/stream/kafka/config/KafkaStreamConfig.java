package com.fancypants.stream.kafka.config;

import java.net.InetAddress;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

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
	private static final String ZOOKEEPER_ENVVAR = "ZOOKEEPER";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ZkClient zkClient;

	@Bean
	public ZkClient zkClient(
			@Value("${" + ZOOKEEPER_ENVVAR + "}") String zookeeper) {
		LOG.trace("zkClient enter {}={}", "zookeeper", zookeeper);
		ZkClient zkClient = new ZkClient(zookeeper);
		zkClient.setZkSerializer(new ZkStringSerializer());
		LOG.trace("zkClient exit {}", zkClient);
		return zkClient;
	}

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

		// ensure that the topic exists
		Assert.isTrue(AdminUtils.topicExists(zkClient, topic));

		// now create the writer
		StreamWriter<RawRecordEntity> streamWriter = new KafkaStreamWriter<RawRecordEntity>(
				objectMapper, producer, RawRecordEntity.class, topic);
		return streamWriter;
	}

	public static final class ZkStringSerializer implements ZkSerializer {
		@Override
		public byte[] serialize(Object o) throws ZkMarshallingError {
			return ZKStringSerializer.serialize(o);
		}

		@Override
		public Object deserialize(byte[] bytes) throws ZkMarshallingError {
			return ZKStringSerializer.deserialize(bytes);
		}
	}
}
