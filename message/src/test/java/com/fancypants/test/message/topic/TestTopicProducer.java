package com.fancypants.test.message.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fancypants.message.exception.AbstractMessageException;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.test.message.exception.TestMessageException;

public class TestTopicProducer implements TopicProducer {

	private final ConnectionFactory factory;
	private final Destination topic;
	private Connection connection;

	public TestTopicProducer(Destination topic) {
		// create the actual factory
		this.factory = new ActiveMQConnectionFactory("vm://localhost");
		// store the topic
		this.topic = topic;
	}

	@Override
	public void sendMessage(final String message) throws AbstractMessageException {
		try {
			// get a session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// allocate a message
			TextMessage msg = session.createTextMessage();
			msg.setText(message);
			// create a producer
			MessageProducer producer = session.createProducer(topic);
			// send it
			producer.send(msg);
			// close the producer
			producer.close();
			// close the session
			session.close();
		} catch (JMSException e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public void start() throws AbstractMessageException {
		try {
			// start the connection
			this.connection = factory.createConnection();
			this.connection.start();
		} catch (JMSException e) {
			throw new TestMessageException(e);
		}
	}

	@Override
	public void close() {
		// cleanup the connection
		if (null != connection) {
			try {
				connection.close();
			} catch (JMSException e) {
				assert false;
			}
		}
	}

}
