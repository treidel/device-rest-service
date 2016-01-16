package com.fancypants.message.rabbitmq.topic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.rabbitmq.exception.RabbitMQException;
import com.fancypants.message.topic.TopicProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

public class RabbitMQTopicProducer implements TopicProducer {
	private static final Logger LOG = LoggerFactory.getLogger(RabbitMQTopicProducer.class);

	private final Channel channel;
	private final String exchange;
	private final String routingKey;

	public RabbitMQTopicProducer(Channel channel, String exchange, String routingKey) {
		LOG.trace("RabbitMQTopicProducer enter {}={} {}={} {}={}", "channel", channel, "exchange", exchange,
				"routingKey", routingKey);
		this.channel = channel;
		this.exchange = exchange;
		this.routingKey = routingKey;
	}

	@Override
	public void sendMessage(String message) throws AbstractMessageException {
		LOG.trace("sendMessage enter {}={}", "message", message);
		try {
			channel.basicPublish(exchange, routingKey, MessageProperties.TEXT_PLAIN, message.getBytes());
		} catch (IOException e) {
			LOG.warn("unable to send message to connection=" + channel.getConnection() + " on exchange=" + exchange);
			throw new RabbitMQException(e);
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
		// close the channel
		try {
			channel.close();
		} catch (IOException e) {
			LOG.error("error closing channel", e);
		}
		LOG.trace("close exit");
	}
}
