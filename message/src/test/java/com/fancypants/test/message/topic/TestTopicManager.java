package com.fancypants.test.message.topic;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.test.message.exception.TestMessageException;

@Component
public class TestTopicManager implements TopicManager, Serializable {

	private static final long serialVersionUID = -3133353523564230430L;

	@Autowired
	private transient BrokerService broker;
	private transient JmsTemplate template;

	@PostConstruct
	private void init() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				"vm://localhost");
		template = new JmsTemplate(factory);
		template.setPubSubDomain(true);
	}

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

		// create the topic
		Destination destination = new ActiveMQTopic(topic);
		// create the producer
		TopicProducer producer = new TestTopicProducer(template, destination);
		return producer;
	}

	@Override
	public TopicConsumer topicConsumer(String topic)
			throws AbstractMessageException {

		// find the topic
		Destination destination = new ActiveMQTopic(topic);
		// create the consumer
		TopicConsumer consumer = new TestTopicConsumer(template, destination);
		return consumer;

	}
}
