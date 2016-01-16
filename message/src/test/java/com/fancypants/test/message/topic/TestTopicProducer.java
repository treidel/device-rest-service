package com.fancypants.test.message.topic;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicProducer;

public class TestTopicProducer implements TopicProducer {

	private final JmsTemplate template;
	private final Destination topic;

	public TestTopicProducer(JmsTemplate template, Destination topic) {
		this.template = template;
		this.topic = topic;
	}

	@Override
	public void sendMessage(final String message)
			throws AbstractMessageException {
		template.send(topic, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {

				TextMessage msg = session.createTextMessage();
				msg.setText(message);
				return msg;
			}
		});
	}

	@Override
	public void start() throws AbstractMessageException {
		// nothing to do as we open a new connection for each message
	}
	
	@Override
	public void close() {
		// nothing to do as we open a new connection for each message
	}

}
