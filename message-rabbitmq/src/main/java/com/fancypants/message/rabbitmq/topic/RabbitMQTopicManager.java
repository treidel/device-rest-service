package com.fancypants.message.rabbitmq.topic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fancypants.message.AbstractMessageException;
import com.fancypants.message.rabbitmq.RabbitMQException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@Component
public class RabbitMQTopicManager implements TopicManager {

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitMQTopicManager.class);

	@Autowired
	private Connection connection;

	@Autowired
	@Qualifier("exchange")
	private String exchange;

	@Override
	public void topicCreate(String topic) throws AbstractMessageException {
		LOG.trace("RabbitMQTopicManager.topicCreate enter" + " topic=" + topic);

		try {
			// allocate a channel
			Channel channel = connection.createChannel();
			// declare the exchange, if it already exists this is a no-op
			channel.exchangeDeclare(exchange, "direct", true);
			// close the channel
			channel.close();
		} catch (IOException e) {
			LOG.error("unable to create exchange", e);
			throw new RabbitMQException(e);
		}

		LOG.trace("RabbitMQTopicManager.topicCreate exit");
	}

	@Override
	public void topicDestroy(String topic) throws AbstractMessageException {
		LOG.trace("RabbitMQTopicManager.topicDestroy enter" + " topic=" + topic);
		// nothing to do since we don't destroy the exchange
		LOG.trace("RabbitMQTopicManager.topicCreate exit");

	}

	@Override
	public TopicProducer topicProducer(String topic)
			throws AbstractMessageException {
		LOG.trace("RabbitMQTopicManager.topicProducer enter topic=" + topic);
		try {
			// create the channel
			Channel channel = connection.createChannel();		
			// wrap the channel 
			TopicProducer producer = new RabbitMQTopicProducer(channel, exchange, topic);
			LOG.trace("RabbitMQTopicManager.topicProducer exit producer="
					+ producer);
			return producer;
		} catch (IOException e) {
			LOG.error("can not create producer", e);
			throw new RabbitMQException(e);
		}
	}

	@Override
	public TopicConsumer topicConsumer(String topic)
			throws AbstractMessageException {
		try {
			// create the channel
			Channel channel = connection.createChannel();
			// allocate the queue
			String queue = channel.queueDeclare().getQueue();
			// bind the queue to the exchange and filter on the topic name
			channel.queueBind(queue, exchange, topic);
			// wrap the channel + queue
			TopicConsumer consumer = new RabbitMQTopicConsumer(channel, queue);
			LOG.trace("RabbitMQTopicManager.topicConsumer exit consumer="
					+ consumer);
			return consumer;
		} catch (IOException e) {
			LOG.error("can not create consumer", e);
			throw new RabbitMQException(e);
		}
	}
}
