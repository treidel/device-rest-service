package com.fancypants.test.message.rabbitmq;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.message.rabbitmq.config.RabbitMQConfig;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = RabbitMQConfig.class)
public class ProducerTests {

	private static final String TEST_TOPIC = "TEST";
	private static final String TEST_MESSAGE = "Hello World";

	private @Autowired
	TopicManager topicManager;

	private @Autowired
	Connection connection;

	private @Autowired
	@Qualifier("exchange")
	String exchange;

	@Before
	public void setup() {

	}

	@After
	public void cleanup() throws Exception {
		// remove the exchange
		Channel channel = connection.createChannel();
		channel.exchangeDelete(exchange);
		channel.close();
	}

	@Test
	public void createTest() throws Exception {
		topicManager.topicCreate(TEST_TOPIC);
	}

	@Test
	public void duplicateCreateTest() throws Exception {
		createTest();
		createTest();
	}

	@Test
	public void deleteTest() throws Exception {
		// run the create test first
		createTest();
		// now delete
		topicManager.topicDestroy(TEST_TOPIC);
	}

	@Test
	public void createProducerTest() throws Exception {
		TopicProducer producer = topicManager.topicProducer(TEST_TOPIC);
		Assert.notNull(producer);
	}

	@Test
	public void publicTest() throws Exception {
		TopicProducer producer = topicManager.topicProducer(TEST_TOPIC);
		producer.sendMessage(TEST_MESSAGE);
	}

}
