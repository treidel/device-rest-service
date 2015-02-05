package com.fancypants.test.message.topic;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.test.message.exception.TestMessageException;

@Component
public class TestTopicManager implements TopicManager {

	@Autowired
	private BrokerService broker;

	@Autowired
	@Qualifier("topicTemplate")
	private JmsTemplate template;
	
	@Override
	public void topicCreate(String topic) throws AbstractMessageException {
		try {
			broker.getAdminView().addTopic(topic);
		} catch (Exception e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public void topicDestroy(String topic) throws AbstractMessageException {
		try {
			broker.getAdminView().removeTopic(topic);
		} catch (Exception e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public TopicProducer topicProducer(String topic)
			throws AbstractMessageException {
		try {
			// find the topic
			Destination destination = findTopic(topic);
			// create the producer
			TopicProducer producer = new TestTopicProducer(template,
					destination);
			return producer;
		} catch (JMSException e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public TopicConsumer topicConsumer(String topic)
			throws AbstractMessageException {
		try {
			// find the topic
			Destination destination = findTopic(topic);
			// create the consumer
			TopicConsumer consumer = new TestTopicConsumer(template,
					destination);
			return consumer;
		} catch (JMSException e) {
			throw new TestMessageException(e);
		}
	}

	private Destination findTopic(String topic) throws JMSException {
		// create a session to do a topic lookup
		Connection connection = template.getConnectionFactory()
				.createConnection();
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		// find the topic
		Destination destination = template.getDestinationResolver()
				.resolveDestinationName(session, topic, true);
		// close the session + connection
		session.close();
		connection.close();
		return destination;
	}
}
