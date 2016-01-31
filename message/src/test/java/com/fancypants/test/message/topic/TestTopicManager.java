package com.fancypants.test.message.topic;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Destination;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.Connection;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.BrokerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.test.message.exception.TestMessageException;

@Component
public class TestTopicManager implements TopicManager, Serializable {

	private static final long serialVersionUID = -3133353523564230430L;

	private transient BrokerService broker;

	@PostConstruct
	private void init() throws Exception {
		// create the broker
		broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		broker.start();
	}

	@PreDestroy
	private void fini() throws Exception {
		// go through all connections and force-stop them
		for (Connection connection : broker.getBroker().getClients()) {
			((TransportConnection) connection).stop();
		}
		// now close the broker
		broker.stop();
	}

	@Override
	public void topicCreate(String topic) throws AbstractMessageException {
		ActiveMQDestination destination = new ActiveMQTopic(topic);
		try {
			broker.getBroker().addDestination(BrokerSupport.getConnectionContext(broker.getBroker()), destination,
					true);
		} catch (Exception e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public void topicDestroy(String topic) throws AbstractMessageException {
		ActiveMQDestination destination = new ActiveMQTopic(topic);
		try {
			broker.getBroker().removeDestination(BrokerSupport.getConnectionContext(broker.getBroker()), destination,
					1000);
		} catch (Exception e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public TopicProducer topicProducer(String topic) throws AbstractMessageException {

		// create the topic
		Destination destination = new ActiveMQTopic(topic);
		// create the producer
		TopicProducer producer = new TestTopicProducer(destination);
		return producer;
	}

	@Override
	public TopicConsumer topicConsumer(String topic, TopicConsumer.Handler handler) throws AbstractMessageException {

		// find the topic
		Destination destination = new ActiveMQTopic(topic);
		// create the consumer
		TopicConsumer consumer = new TestTopicConsumer(destination, handler);
		return consumer;

	}
}
