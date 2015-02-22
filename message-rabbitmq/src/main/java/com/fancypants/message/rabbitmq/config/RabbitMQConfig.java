package com.fancypants.message.rabbitmq.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.fancypants.message.rabbitmq.topic.RabbitMQTopicManager;
import com.fancypants.message.topic.TopicManager;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitMQConfig {
	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitMQConfig.class);

	@Bean
	public Connection connection() throws Exception {
		LOG.trace("RabbitMQConfig.connection enter");
		ConnectionFactory factory = new ConnectionFactory();
		factory.setPassword(getPassword());
		factory.setUri(getURI());
		factory.setAutomaticRecoveryEnabled(true);
		Connection connection = factory.newConnection();
		LOG.trace("RabbitMQConfig.connection exit connection=" + connection);
		return connection;
	}

	@Bean
	public String exchange() {
		return System.getProperty("rabbitmq.exchange");
	}

	@Bean
	public TopicManager topicManager() {
		LOG.trace("RabbitMQConfig.topicManager enter");
		TopicManager manager = new RabbitMQTopicManager();
		LOG.trace("RabbitMQConfig.topicManager exit manager=" + manager);
		return manager;
	}

	private URI getURI() {
		String uri = System.getProperty("rabbitmq.uri");
		Assert.notNull(uri);
		return URI.create(uri);
	}
	
	private String getPassword() {
		String password = System.getProperty("rabbitmq.password");
		Assert.notNull(password);
		return password;
	}

}
