package com.fancypants.test.message.rabbitmq.suite;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.message.rabbitmq.config.RabbitMQConfig;
import com.fancypants.test.message.suite.MessageTests;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = RabbitMQConfig.class)
public class RabbitMQMessageTests extends MessageTests {

	private @Autowired
	Connection connection;

	private @Autowired
	@Qualifier("exchange")
	String exchange;

	@After
	public void cleanup() throws Exception {
		// remove the exchange
		Channel channel = connection.createChannel();
		channel.exchangeDelete(exchange);
		channel.close();
	}
}
