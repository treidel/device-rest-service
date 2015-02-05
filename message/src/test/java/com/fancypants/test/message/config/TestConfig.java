package com.fancypants.test.message.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import com.fancypants.test.message.TestMessageScanMe;

@Configuration
@ComponentScan(basePackageClasses = { TestMessageScanMe.class })
public class TestConfig {

	@Bean
	public BrokerService brokerService() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.start();
		return broker;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				"vm://localhost");
		return factory;
	}

	@Bean(name = "topicTemplate")
	public JmsTemplate jmsTopicTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
		jmsTemplate.setPubSubDomain(true);
		return jmsTemplate;
	}

	@Bean(name = "queueTemplate")
	public JmsTemplate jmsQueueTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
		jmsTemplate.setPubSubDomain(false);
		return jmsTemplate;
	}
}
