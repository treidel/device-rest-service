package com.fancypants.stream.kinesis.writer;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.fancypants.stream.writer.StreamWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KinesisStreamWriter<T> implements StreamWriter<T> {

	private static final Logger LOG = LoggerFactory.getLogger(KinesisStreamWriter.class);
	
	private final AmazonKinesis amazonKinesis;
	private final ObjectMapper objectMapper;
	private final String streamName;

	public KinesisStreamWriter(ObjectMapper objectMapper,
			AmazonKinesis amazonKinesis, Class<T> clazz, String streamName) {
		LOG.trace("KinesisStreamWriter entry", objectMapper, amazonKinesis, clazz, streamName);
		// ensure the class is serializable
		if (false == objectMapper.canSerialize(clazz)) {
			LOG.error("can not serialize", clazz);
			throw new IllegalAccessError("can not serialize " + clazz);
		}
		this.objectMapper = objectMapper;
		this.amazonKinesis = amazonKinesis;
		this.streamName = streamName;
		LOG.trace("KinesisStreamWriter exit");
	}

	public void putRecord(String hashKey, T record) {
		LOG.trace("putRecord entry", hashKey, record);
		try {
			// serialize
			byte[] data = objectMapper.writeValueAsBytes(record);
			// queue
			amazonKinesis.putRecord(streamName, ByteBuffer.wrap(data), hashKey);
		} catch (JsonProcessingException e) {
			LOG.error("processing error", e);
		}
		LOG.trace("putRecord exit");
	}

}
