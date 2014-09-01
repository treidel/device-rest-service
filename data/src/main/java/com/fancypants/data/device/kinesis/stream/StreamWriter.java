package com.fancypants.data.device.kinesis.stream;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.Record;

public class StreamWriter {

	private final AmazonKinesis amazonKinesis;
	private final String streamName;

	public StreamWriter(AmazonKinesis amazonKinesis, String streamName) {
		this.amazonKinesis = amazonKinesis;
		this.streamName = streamName;
	}

	public void putRecord(Record record) {
		// queue
		amazonKinesis.putRecord(streamName, record.getData(),
				record.getPartitionKey());
	}

}
