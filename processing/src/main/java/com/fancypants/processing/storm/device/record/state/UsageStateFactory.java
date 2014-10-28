package com.fancypants.processing.storm.device.record.state;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fancypants.data.device.dynamodb.repository.DynamoDBHourlyRecordRepository;
import com.fancypants.processing.storm.device.record.auth.CustomAWSCredentialsProvider;

@Component
public class UsageStateFactory implements StateFactory {

	private static final long serialVersionUID = 296987272885779417L;

	@Autowired
	private DynamoDBHourlyRecordRepository repository;

	@Autowired
	private CustomAWSCredentialsProvider credentialsProvider;

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// create the database client
		AmazonDynamoDBClient client = new AmazonDynamoDBClient(
				credentialsProvider.getCredentials());
		// fill in the unserialized field in the repository
		repository.setDynamoDB(new DynamoDB(client));
		// create the backing map
		UsageState state = new UsageState(repository);
		return state;
	}

}
