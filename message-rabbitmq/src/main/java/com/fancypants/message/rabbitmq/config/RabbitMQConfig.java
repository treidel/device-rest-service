package com.fancypants.message.rabbitmq.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fancypants.message.rabbitmq.topic.RabbitMQTopicManager;
import com.fancypants.message.topic.TopicManager;

@Configuration
public class RabbitMQConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitMQConfig.class);

	public static final String RABBITMQ_URI_ENVVAR = "RABBITMQ_URI";
	public static final String RABBITMQ_PASSWORD_ENVVAR = "RABBITMQ_PASSWORD";
	public static final String RABBITMQ_EXCHANGE_ENVVAR = "RABBITMQ_EXCHANGE";

	@Bean
	public TopicManager topicManager(
			@Value("${" + RABBITMQ_URI_ENVVAR + "}") String uri, @Value("${"
					+ RABBITMQ_PASSWORD_ENVVAR + "}") String password,
			@Value("${" + RABBITMQ_EXCHANGE_ENVVAR + "}") String exchange)
			throws Exception {
		LOG.trace("topicManager enter", "uri", uri, "password", password,
				"exchange", exchange);
		TopicManager manager = new RabbitMQTopicManager(URI.create(uri),
				password, exchange);
		LOG.trace("topicManager exit {}", manager);
		return manager;
	}

}
