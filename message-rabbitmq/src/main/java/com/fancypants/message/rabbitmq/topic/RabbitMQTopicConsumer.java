package com.fancypants.message.rabbitmq.topic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.rabbitmq.exception.RabbitMQException;
import com.fancypants.message.topic.TopicConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQTopicConsumer implements TopicConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(RabbitMQTopicConsumer.class);

	private final Channel channel;
	private final String queue;
	private final Handler handler;

	public RabbitMQTopicConsumer(Channel channel, String queue, Handler handler) {
		LOG.trace("RabbitMQTopicConsumer enter {}={} {}={}", "channel", channel, "queue", queue, "handler", handler);
		// store data
		this.channel = channel;
		this.queue = queue;
		this.handler = handler;
		LOG.trace("RabbitMQTopicConsumer exit");
	}

	@Override
	public void start() throws AbstractMessageException {
		LOG.trace("start enter");
		// let the channel do its thing
		try {
			channel.basicConsume(queue, new MessageConsumer(channel, handler));
		} catch (IOException e) {
			LOG.error("unable to receive message", e);
			throw new RabbitMQException(e);
		}
		LOG.trace("start exit");
	}

	@Override
	public void close() {
		LOG.trace("close enter");
		// close the channel
		try {
			channel.close();
		} catch (IOException e) {
			LOG.error("unable to close channel", e);
		}
		LOG.trace("close exit");
	}

	private class MessageConsumer extends DefaultConsumer {

		private final Handler handler;

		public MessageConsumer(Channel channel, Handler handler) {
			super(channel);
			this.handler = handler;
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			LOG.trace("MessageConsumer.handleDelivery enter {}={} {}={} {}={}", "consumerTag", consumerTag, "envelope",
					envelope, "properties", properties, "body", body);
			// convert the body into a string
			String message = new String(body);
			// handle it
			handler.handle(message);
			LOG.trace("MessageConsumer.handleDelivery exit");
		}
	}
}
