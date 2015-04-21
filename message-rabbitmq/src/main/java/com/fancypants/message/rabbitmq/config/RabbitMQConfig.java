package com.fancypants.message.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.config.util.ConfigUtils;
import com.fancypants.message.rabbitmq.topic.RabbitMQTopicManager;
import com.fancypants.message.topic.TopicManager;

@Configuration
public class RabbitMQConfig {

	private static final String RABBITMQ_EXCHANGE_ENVVAR = "RABBITMQ_EXCHANGE";

	private static final Logger LOG = LoggerFactory
			.getLogger(RabbitMQConfig.class);

	@Bean
	public String exchange() {
		return ConfigUtils.retrieveEnvVarOrFail(RABBITMQ_EXCHANGE_ENVVAR);
	}

	@Bean
	public TopicManager topicManager() {
		LOG.trace("RabbitMQConfig.topicManager enter");
		TopicManager manager = new RabbitMQTopicManager();
		LOG.trace("RabbitMQConfig.topicManager exit manager=" + manager);
		return manager;
	}

}
