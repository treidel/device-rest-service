package com.fancypants.message.sns.topic;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.regions.Region;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;

public class SNSTopicManager implements TopicManager {

	private static final Logger LOG = LoggerFactory.getLogger(SNSTopicManager.class);

	private static final String SNS_TOPIC_PREFIX_ENVVAR = "SNS_TOPIC_PREFIX";

	@Autowired
	private AmazonSNSClient amazonSNSClient;

	@Autowired
	private AmazonSQSClient amazonSQSClient;

	@Autowired
	@Value("${" + SNS_TOPIC_PREFIX_ENVVAR + "}")
	private String topicPrefix;

	@Autowired
	private Region awsSNSRegion;

	@Autowired
	@Qualifier("amazonAccountIdentifier")
	private String accountId;

	@Override
	public void topicCreate(String topic) throws AbstractMessageException {
		LOG.trace("topicCreate enter {}={}", topic);
		// create the topic name
		String topicName = topicPrefix + topic;
		// create the topic
		LOG.debug("creating topic with topicName={}", topicName);
		amazonSNSClient.createTopic(topicName);
		LOG.trace("topicCreate exit");
	}

	@Override
	public void topicDestroy(String topic) throws AbstractMessageException {
		LOG.trace("topicDestroy enter {}={}", topic);
		// create the topic ARN
		String topicARN = computeTopicARN(topic);
		// create the topic
		LOG.debug("deleting topic with topicARN={}", topicARN);
		amazonSNSClient.deleteTopic(topicARN);
		LOG.trace("topicDestroy exit");
	}

	@Override
	public TopicProducer topicProducer(String topic) throws AbstractMessageException {
		LOG.trace("topicProducer enter {}={}", "topic", topic);
		// compute the ARN
		String topicARN = computeTopicARN(topic);
		// make sure its a real topic
		amazonSNSClient.getTopicAttributes(topicARN);
		// create the producer
		TopicProducer producer = new SNSTopicProducer(amazonSNSClient, topicARN);
		LOG.trace("topicProducer exit {}", producer);
		return producer;
	}

	@Override
	public TopicConsumer topicConsumer(String topic) throws AbstractMessageException {
		LOG.trace("topicConsumer enter {}={}", "topic", topic);
		// compute the ARN
		String topicARN = computeTopicARN(topic);
		// allocate a new SQS queue with a random name
		UUID uuid = UUID.randomUUID();
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(uuid.toString());
		createQueueRequest.addAttributesEntry("MessageRetentionPeriod", String.valueOf(60));
		CreateQueueResult createQueueResult = amazonSQSClient.createQueue(createQueueRequest);
		String queueURL = createQueueResult.getQueueUrl();
		LOG.debug("subscribing queue={} to topic={}", queueURL, topicARN);
		String subscriptionARN = Topics.subscribeQueue(amazonSNSClient, amazonSQSClient, topicARN, queueURL);
		// create the consumer
		TopicConsumer consumer = new SNSTopicConsumer(amazonSNSClient, amazonSQSClient, subscriptionARN, queueURL);
		LOG.trace("topicConsumer exit {}", consumer);
		return consumer;
	}

	private String computeTopicARN(String topic) {
		// first compute the name
		String topicName = topicPrefix + topic;
		// create the topic ARN
		String topicARN = "arn:aws:sns:" + awsSNSRegion.getName() + ":" + accountId + ":" + topicName;
		return topicARN;
	}

}
