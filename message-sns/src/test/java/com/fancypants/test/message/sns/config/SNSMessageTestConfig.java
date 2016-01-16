package com.fancypants.test.message.sns.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.fancypants.common.CommonScanMe;
import com.fancypants.message.sns.MessageSNSScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, MessageSNSScanMe.class })
@PropertySource("classpath:/test.properties")
public class SNSMessageTestConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Autowired
	private AmazonSNSClient amazonSNSClient;

	@Autowired
	private AmazonSQSClient amazonSQSClient;

	@PostConstruct
	private void init() throws Exception {
		// make sure we can connect
		amazonSNSClient.listTopics();
	}

	@PreDestroy
	private void fini() throws Exception {
		// remove all queues in our region
		ListQueuesResult result = amazonSQSClient.listQueues();
		for (String queueURL : result.getQueueUrls()) {
			amazonSQSClient.deleteQueue(queueURL);
		}
	}
}
