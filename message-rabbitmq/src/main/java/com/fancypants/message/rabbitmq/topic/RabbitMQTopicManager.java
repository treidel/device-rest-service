package com.fancypants.message.rabbitmq.topic;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.rabbitmq.exception.RabbitMQException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Component
public class RabbitMQTopicManager implements TopicManager, Serializable {

	private static final long serialVersionUID = 2706248108381878149L;

	public static final String RABBITMQ_URI_ENVVAR = "RABBITMQ_URI";
	public static final String RABBITMQ_PASSWORD_ENVVAR = "RABBITMQ_PASSWORD";
	public static final String RABBITMQ_EXCHANGE_ENVVAR = "RABBITMQ_EXCHANGE";

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitMQTopicManager.class);

	@Autowired
	@Value("${" + RABBITMQ_EXCHANGE_ENVVAR + "}")
	private String exchange;

	private transient Connection connection;

	@PostConstruct
	private void init() throws Exception {
		LOG.trace("init enter");
		// create the connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setPassword(getPassword());
		factory.setUri(getURI());
		factory.setAutomaticRecoveryEnabled(true);
		connection = factory.newConnection();
		// create the exchange (if it doesn't already exist)
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

		LOG.trace("init exit");
	}

	@Override
	public void topicCreate(String topic) throws AbstractMessageException {
		LOG.trace("topicCreate enter {}={}", "topic", topic);
		// nothing to do since we pre create the exchange
		LOG.trace("topicCreate exit");
	}

	@Override
	public void topicDestroy(String topic) throws AbstractMessageException {
		LOG.trace("topicDestroy enter {}={}", "topic", topic);
		// nothing to do since we don't destroy the exchange
		LOG.trace("topicCreate exit");

	}

	@Override
	public TopicProducer topicProducer(String topic)
			throws AbstractMessageException {
		LOG.trace("topicProducer enter {}={}", "topic", topic);
		try {
			// create the channel
			Channel channel = connection.createChannel();
			// wrap the channel
			TopicProducer producer = new RabbitMQTopicProducer(channel,
					exchange, topic);
			LOG.trace("topicProducer exit {}={}", "producer", producer);
			return producer;
		} catch (IOException e) {
			LOG.error("can not create producer", e);
			throw new RabbitMQException(e);
		}
	}

	@Override
	public TopicConsumer topicConsumer(String topic)
			throws AbstractMessageException {
		LOG.trace("topicConsumer enter {}={}", "topic", topic);
		try {
			// create the channel
			Channel channel = connection.createChannel();
			// allocate the queue
			String queue = channel.queueDeclare().getQueue();
			// bind the queue to the exchange and filter on the topic name
			channel.queueBind(queue, exchange, topic);
			// wrap the channel + queue
			TopicConsumer consumer = new RabbitMQTopicConsumer(channel, queue);
			LOG.trace("topicConsumer exit {}", consumer);
			return consumer;
		} catch (IOException e) {
			LOG.error("can not create consumer", e);
			throw new RabbitMQException(e);
		}
	}

	private URI getURI() {
		LOG.trace("getURI enter");
		String value = ConfigUtils.retrieveEnvVarOrFail(RABBITMQ_URI_ENVVAR);
		URI uri = URI.create(value);
		LOG.trace("getURI exit {}", uri);
		return uri;
	}

	private String getPassword() {
		LOG.trace("getPassword enter");
		String password = ConfigUtils
				.retrieveEnvVarOrFail(RABBITMQ_PASSWORD_ENVVAR);
		LOG.trace("getPassword exit {}", password);
		return password;
	}
}
