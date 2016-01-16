package com.fancypants.message.sns.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.sns.exception.SNSException;
import com.fancypants.message.topic.TopicProducer;

public class SNSTopicProducer implements TopicProducer {

	private static final Logger LOG = LoggerFactory.getLogger(SNSTopicProducer.class);

	private final AmazonSNSClient client;
	private final String topicARN;

	public SNSTopicProducer(AmazonSNSClient client, String topicARN) {
		LOG.trace("SNSTopicProducer enter {}={} {}={}", "client", client, "topicARN", topicARN);
		this.client = client;
		this.topicARN = topicARN;
		LOG.trace("SNSTopicProducer exit");
	}

	@Override
	public void sendMessage(String message) throws AbstractMessageException {
		LOG.trace("sendMessage enter {}={}", "message", message);
		try {
			client.publish(this.topicARN, message);
		} catch (Throwable t) {
			// any exception is a problem
			LOG.error("exception={}", t);
			throw new SNSException(t);
		}
		LOG.trace("sendMessage exit");
	}

	@Override
	public void start() throws AbstractMessageException {
		LOG.trace("start enter");
		LOG.trace("start exit");
	}

	@Override
	public void close() {
		LOG.trace("close enter");
		LOG.trace("close exit");
	}

}
