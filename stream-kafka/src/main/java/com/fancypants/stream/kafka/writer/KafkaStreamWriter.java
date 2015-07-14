package com.fancypants.stream.kafka.writer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fancypants.stream.exception.StreamException;
import com.fancypants.stream.writer.StreamWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaStreamWriter<T> implements StreamWriter<T> {

	private static final Logger LOG = LoggerFactory
			.getLogger(KafkaStreamWriter.class);

	private final Producer<String, byte[]> producer;
	private final ObjectMapper objectMapper;
	private final String topic;

	public KafkaStreamWriter(ObjectMapper objectMapper,
			Producer<String, byte[]> producer, Class<T> clazz, String topic) {
		LOG.trace("KinesisStreamWriter entry {}={} {}={} {}={} {}={}",
				"objectMapper", objectMapper, "producer", producer, "clazz",
				clazz, "topic", topic);
		// ensure the class is serializable
		Assert.isTrue(objectMapper.canSerialize(clazz));

		this.objectMapper = objectMapper;
		this.producer = producer;
		this.topic = topic;

		LOG.trace("KinesisStreamWriter exit");
	}

	public void putRecord(String hashKey, T record) throws StreamException {
		LOG.trace("putRecord entry", hashKey, record);
		try {
			// serialize
			byte value[] = objectMapper.writeValueAsBytes(record);
			// wrap in kafka class
			ProducerRecord<String, byte[]> message = new ProducerRecord<>(
					topic, hashKey, value);
			// queue
			Future<RecordMetadata> future = producer.send(message);
			// wait a while for the record to write
			future.get(1L, TimeUnit.SECONDS);
		} catch (JsonProcessingException e) {
			LOG.error("processing error", e);
			throw new StreamException(e);
		} catch (InterruptedException e) {
			LOG.error("processing error", e);
			throw new StreamException(e);
		} catch (ExecutionException e) {
			LOG.error("processing error", e);
			throw new StreamException(e);
		} catch (TimeoutException e) {
			LOG.warn("timeout on send for hashKey={}", hashKey);
			throw new StreamException(e);
		}
		LOG.trace("putRecord exit");
	}
}
