package com.fancypants.test.message.rabbitmq.suite;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.message.rabbitmq.config.RabbitMQConfig;
import com.fancypants.message.rabbitmq.topic.RabbitMQTopicManager;
import com.fancypants.test.message.suite.MessageTests;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(inheritLocations = false, loader = AnnotationConfigContextLoader.class, classes = RabbitMQConfig.class)
public class RabbitMQMessageTests extends MessageTests {

	private Connection connection;

	private @Autowired
	@Qualifier("exchange")
	String exchange;

	@PostConstruct
	private void init() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		String password = ConfigUtils
				.retrieveEnvVarOrFail(RabbitMQTopicManager.RABBITMQ_PASSWORD_ENVVAR);
		factory.setPassword(password);
		URI uri = URI
				.create(ConfigUtils
						.retrieveEnvVarOrFail(RabbitMQTopicManager.RABBITMQ_URI_ENVVAR));
		factory.setUri(uri);
		factory.setAutomaticRecoveryEnabled(true);
		connection = factory.newConnection();
	}

	@After
	public void cleanup() throws Exception {
		// remove the exchange
		Channel channel = connection.createChannel();
		channel.exchangeDelete(exchange);
		channel.close();
	}
}
