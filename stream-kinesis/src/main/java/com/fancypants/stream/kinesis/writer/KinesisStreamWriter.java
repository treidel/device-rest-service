package com.fancypants.stream.kinesis.writer;

import java.nio.ByteBuffer;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.fancypants.stream.writer.StreamWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KinesisStreamWriter<T> implements StreamWriter<T> {

	private final AmazonKinesis amazonKinesis;
	private final ObjectMapper objectMapper;
	private final String streamName;

	public KinesisStreamWriter(ObjectMapper objectMapper,
			AmazonKinesis amazonKinesis, Class<T> clazz, String streamName) {
		// ensure the class is serializable
		if (false == objectMapper.canSerialize(clazz)) {
			throw new IllegalAccessError("can not serialize " + clazz);
		}
		this.objectMapper = objectMapper;
		this.amazonKinesis = amazonKinesis;
		this.streamName = streamName;
	}

	public void putRecord(String hashKey, T record) {
		try {
			// serialize
			byte[] data = objectMapper.writeValueAsBytes(record);
			// queue
			amazonKinesis.putRecord(streamName, ByteBuffer.wrap(data), hashKey);
		} catch (JsonProcessingException e) {
			// TBD: log
		}
	}

}
