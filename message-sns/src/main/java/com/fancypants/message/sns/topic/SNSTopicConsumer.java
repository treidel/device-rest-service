package com.fancypants.message.sns.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;

public class SNSTopicConsumer implements TopicConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(SNSTopicConsumer.class);
	private static final int AMAZON_SQS_WAIT_TIME_IN_SECS = 20;

	private final AmazonSNSClient snsClient;
	private final AmazonSQSClient sqsClient;
	private final String subscriptionARN;
	private final String sqsURL;

	public SNSTopicConsumer(AmazonSNSClient snsClient, AmazonSQSClient sqsClient, String subscriptionARN,
			String sqsURL) {
		LOG.trace("SNSTopicConsumer enter {}={} {}={} {}={} {}={}", "snsClient", snsClient, "sqsClient", sqsClient,
				"subscriptionARN", subscriptionARN, "sqsURL", sqsURL);
		this.snsClient = snsClient;
		this.sqsClient = sqsClient;
		this.subscriptionARN = subscriptionARN;
		this.sqsURL = sqsURL;
		LOG.trace("SNSTopicConsumer exit");
	}

	@Override
	public void receiveMessages(Handler handler) throws AbstractMessageException {
		LOG.trace("receiveMessage enter {}={}", "handler", handler);
		ReceiveMessageRequest request = new ReceiveMessageRequest(sqsURL);
		request.setWaitTimeSeconds(AMAZON_SQS_WAIT_TIME_IN_SECS);
		ReceiveMessageResult result = sqsClient.receiveMessage(request);
		for (Message message : result.getMessages()) {
			LOG.debug("received message={}", message);
			handler.handle(message.getBody());
		}
		LOG.trace("receiveMessage exit");
	}

	@Override
	public void close() {
		LOG.trace("close enter");
		// first delete the subscription
		snsClient.unsubscribe(this.subscriptionARN);
		// now delete the queue
		sqsClient.deleteQueue(sqsURL);
		LOG.trace("close exit");

	}

}
