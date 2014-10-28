package com.fancypants.test.processing.storm.device.record.test.cases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.model.StreamStatus;
import com.fancypants.stream.kinesis.StreamKinesisScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class,
		StreamKinesisScanMe.class })
public class TestConfiguration {

	@Autowired
	private AmazonKinesis amazonKinesis;

	@Bean
	public Void streamInitialization() {
		// fetch the stream name
		String streamName = getStreamName();
		// delete then create
		blockingDeleteStream(streamName);
		blockingCreateStream(streamName);
		return null;
	}

	@Bean
	public String tablePrefix() {
		return "test";
	}

	private void blockingDeleteStream(String streamName) {
		try {
			// call to delete
			amazonKinesis.deleteStream(streamName);
		} catch (ResourceNotFoundException e) {
			// doesn't exists so we're done
			return;
		}
		// iterate to make sure its gone
		boolean exists = true;
		do {
			try {
				amazonKinesis.deleteStream(streamName);
				Thread.sleep(1000);
			} catch (ResourceNotFoundException e) {
				exists = false;
			} catch (InterruptedException e) {
				// bail out
				return;
			}
		} while (true == exists);
	}

	private void blockingCreateStream(String streamName) {
		// create the stream
		amazonKinesis.createStream(streamName, 1);

		// wait until it's ready
		DescribeStreamResult result = amazonKinesis.describeStream(streamName);
		while (true == result.getStreamDescription().getStreamStatus()
				.equals(StreamStatus.CREATING.toString())) {
			try {
				Thread.sleep(1000);
				result = amazonKinesis.describeStream(streamName);
			} catch (InterruptedException e) {
				// bail out
				return;
			}

		}
	}

	private String getStreamName() {
		return System.getProperty("amazon.kinesis.stream.rawrecord");
	}
}
