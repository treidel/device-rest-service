package com.fancypants.test.message.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.test.message.TestMessageScanMe;

@Configuration
@ComponentScan(basePackageClasses = { TestMessageScanMe.class })
public class MessageTestConfig {

	@Bean
	public BrokerService brokerService() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.start();
		return broker;
	}
}
