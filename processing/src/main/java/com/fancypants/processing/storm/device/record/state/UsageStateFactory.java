package com.fancypants.processing.storm.device.record.state;

import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fancypants.data.device.dynamodb.repository.DynamoDBHourlyRecordRepository;
import com.fancypants.data.device.repository.HourlyRecordRepository;
import com.fancypants.processing.storm.device.record.auth.CustomAWSCredentialsProvider;

public class UsageStateFactory implements StateFactory {

	private static final long serialVersionUID = 296987272885779417L;

	private final CustomAWSCredentialsProvider credentialProvider;

	public UsageStateFactory(CustomAWSCredentialsProvider credentialProvider) {
		this.credentialProvider = credentialProvider;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// create the client
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(
				credentialProvider);
		// create the repository
		HourlyRecordRepository repository = new DynamoDBHourlyRecordRepository(
				client);
		// create the backing map
		UsageState state = new UsageState(repository);
		return state;
	}

}
