package com.fancypants.message.rabbitmq.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

	private URI getURI() {
		return URI.create(System.getProperty("rabbitmq.uri"));
	}

}
