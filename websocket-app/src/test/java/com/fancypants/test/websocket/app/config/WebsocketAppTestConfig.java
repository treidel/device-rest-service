package com.fancypants.test.websocket.app.config;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.message.topic.TopicManager;
import com.fancypants.message.topic.TopicProducer;
import com.fancypants.rest.domain.RawMeasurement;
import com.fancypants.rest.domain.RawRecord;
import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.data.values.RawRecordValues;
import com.fancypants.test.message.TestMessageScanMe;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackageClasses = { WebsocketAppTestConfig.class,
		TestDataScanMe.class, TestMessageScanMe.class })
public class WebsocketAppTestConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(WebsocketAppTestConfig.class);

	@Autowired
	private TopicManager topicManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	@ConditionalOnClass(Test.class)
	public WebSocketClient websocketClient() {
		JettyWebSocketClient client = new JettyWebSocketClient();
		client.start();
		return client;
	}

	@ConditionalOnMissingClass(Test.class)
	@Scheduled(fixedRate = 10000)
	public void trigger() throws Exception {
		LOG.info("trigger");
		// setup the test measurements
		Random rand = new Random();
		Set<RawMeasurement> measurements = new HashSet<RawMeasurement>();
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			RawMeasurement measurement = new RawMeasurement("1-" + i, 120.0f,
					rand.nextFloat() * 100);
			measurements.add(measurement);
		}
		RawRecord record = new RawRecord(UUID.randomUUID(), new Date(), 10.0f,
				measurements);
		TopicProducer topicProducer = topicManager
				.topicProducer(RawRecordValues.RECORD1.getDevice());
		String message = objectMapper.writeValueAsString(record);
		topicProducer.sendMessage(message);
		topicProducer.close();
	}
}
