package com.fancypants.processing.storm.device.record.state;

import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fancypants.data.device.dynamodb.repository.DynamoDBHourlyRecordRepository;
import com.fancypants.data.device.repository.HourlyRecordRepository;

public class UsageStateFactory implements StateFactory {

	private static final long serialVersionUID = 296987272885779417L;

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// TBD: extract credentials from conf
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();
		HourlyRecordRepository repository = new DynamoDBHourlyRecordRepository(
				client);
		return new UsageState(repository);
	}

}
