package com.fancypants.message.rabbitmq.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.config.util.ConfigUtils;
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
	public TopicManager topicManager() throws Exception {
		LOG.trace("topicManager enter");
		TopicManager manager = new RabbitMQTopicManager(getURI(),
				getPassword(), getExchange());
		LOG.trace("topicManager exit {}", manager);
		return manager;
	}

	private String getExchange() {
		LOG.trace("getExchange enter");
		String exchange = ConfigUtils
				.retrieveEnvVarOrFail(RABBITMQ_EXCHANGE_ENVVAR);
		LOG.trace("getExchange exit {}", exchange);
		return exchange;
	}

	private URI getURI() {
		LOG.trace("getURI enter");
		String value = ConfigUtils.retrieveEnvVarOrFail(RABBITMQ_URI_ENVVAR);
		URI uri = URI.create(value);
		LOG.trace("getURI exit {}", uri);
		return uri;
	}

	private String getPassword() {
		LOG.trace("getPassword enter");
		String password = ConfigUtils
				.retrieveEnvVarOrFail(RABBITMQ_PASSWORD_ENVVAR);
		LOG.trace("getPassword exit {}", password);
		return password;
	}

}
