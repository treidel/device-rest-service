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

	private static TestConfiguration singleton;

	public TestConfiguration() {
		singleton = this;
	}

	@Autowired
	private AmazonKinesis amazonKinesis;

	@Bean
	public Void streamInitialization() {
		// delete then create
		blockingDeleteStream();
		blockingCreateStream();
		return null;
	}

	@Bean
	public String tablePrefix() {
		return "test";
	}

	@Bean
	public String streamName() {
		return System.getProperty("amazon.kinesis.stream.rawrecord");
	}

	public static void blockingDeleteStream() {
		// fetch stream name
		String streamName = singleton.streamName();
		try {
			// call to delete
			singleton.amazonKinesis.deleteStream(streamName);
		} catch (ResourceNotFoundException e) {
			// doesn't exists so we're done
			return;
		}
		// iterate to make sure its gone
		boolean exists = true;
		do {
			try {
				singleton.amazonKinesis.deleteStream(streamName);
				Thread.sleep(1000);
			} catch (ResourceNotFoundException e) {
				exists = false;
			} catch (InterruptedException e) {
				// bail out
				return;
			}
		} while (true == exists);
	}

	public static void blockingCreateStream() {
		// fetch stream name
		String streamName = singleton.streamName();
		// create the stream
		singleton.amazonKinesis.createStream(streamName, 1);

		// wait until it's ready
		DescribeStreamResult result = singleton.amazonKinesis
				.describeStream(streamName);
		while (true == result.getStreamDescription().getStreamStatus()
				.equals(StreamStatus.CREATING.toString())) {
			try {
				Thread.sleep(1000);
				result = singleton.amazonKinesis.describeStream(streamName);
			} catch (InterruptedException e) {
				// bail out
				return;
			}

		}
	}

	public static void nonblockingDeleteStream() {
		// fetch stream name
		String streamName = singleton.streamName();
		try {
			// call to delete
			singleton.amazonKinesis.deleteStream(streamName);
		} catch (ResourceNotFoundException e) {
			// doesn't exists so we're done
			return;
		}
	}
}
