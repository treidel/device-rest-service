package com.fancypants.test.message.topic;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;

public class TestTopicConsumer implements TopicConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(TestTopicConsumer.class);

	private final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
	private final TopicConsumer.Handler handler;

	public TestTopicConsumer(Destination topic, final Handler handler) {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
		// setup the container
		container.setConnectionFactory(factory);
		container.setDestination(topic);
		// store the handler
		this.handler = handler;
		// create the receiver helper
		container.setMessageListener(new TestMessageListener());
	}

	@Override
	public void start() throws AbstractMessageException {
		// start the receiver
		container.start();
	}

	@Override
	public void close() {
		container.stop();
	}

	private class TestMessageListener implements MessageListener {
		@Override
		public void onMessage(Message message) {
			// it's always a text message
			TextMessage textMessage = (TextMessage) message;
			try {
				String payload = textMessage.getText();
				// call the handler
				handler.handle(payload);
			} catch (JMSException e) {
				LOG.error("error on receive", e);
			}

		}
	}
}
