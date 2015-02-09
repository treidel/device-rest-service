package com.fancypants.test.message.suite;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.fancypants.message.topic.TopicConsumer;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.test.message.config.MessageTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = MessageTestConfig.class)
public class MessageTests {

	private static final String TEST_TOPIC = "TEST";
	private static final String TEST_MESSAGE = "Hello World";

	private @Autowired
	TopicManager topicManager;

	@After
	public void after() throws Exception {
		topicManager.topicDestroy(TEST_TOPIC);
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
	public void doubleDeleteTest() throws Exception {
		deleteTest();
		topicManager.topicDestroy(TEST_TOPIC);
	}

	@Test
	public void createProducerTest() throws Exception {
		createTest();
		TopicProducer producer = topicManager.topicProducer(TEST_TOPIC);
		Assert.notNull(producer);
	}

	@Test
	public void publishTest() throws Exception {
		createTest();
		TopicProducer producer = topicManager.topicProducer(TEST_TOPIC);
		producer.sendMessage(TEST_MESSAGE);
	}
	
	@Test
	public void publishAndConsumeTest() throws Exception {
		// create the topic
		createTest();
		// setup a counter that is triggered when a message is received
		final AtomicInteger counter = new AtomicInteger(0);
		// create a producer + consumer
		TopicProducer producer = topicManager.topicProducer(TEST_TOPIC);
		TopicConsumer consumer = topicManager.topicConsumer(TEST_TOPIC);
		// setup a semaphore to trigger when the message is received
		final Semaphore semaphore = new Semaphore(0);
		// setup the message handler
		consumer.receiveMessages(new TopicConsumer.Handler() {

			@Override
			public void handle(String message) {
				Assert.isTrue(TEST_MESSAGE.equals(message));
				counter.incrementAndGet();
				semaphore.release();
			} });
		// send the message
		producer.sendMessage(TEST_MESSAGE);
		// wait a little while for the response
		semaphore.tryAcquire(1, TimeUnit.SECONDS);
		// make sure we got a response
		Assert.isTrue(1 == counter.get());
	}

}
