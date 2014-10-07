package com.fancypants.stream.kinesis.writer;

import java.nio.ByteBuffer;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.fancypants.stream.kinesis.entity.KinesisRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamWriter<T extends KinesisRecord> {

	private final AmazonKinesis amazonKinesis;
	private final ObjectMapper objectMapper;
	private final String streamName;

	public StreamWriter(ObjectMapper objectMapper, AmazonKinesis amazonKinesis,
			String streamName) {
		this.objectMapper = objectMapper;
		this.amazonKinesis = amazonKinesis;
		this.streamName = streamName;
	}

	public void putRecord(T record) {
		try {
			// serialize
			byte[] data = objectMapper.writeValueAsBytes(record);
			// queue
			amazonKinesis.putRecord(streamName, ByteBuffer.wrap(data), record.getPartitionKey());
		} catch (JsonProcessingException e) {
			// TBD: log
		}
	}

}
