package com.fancypants.websocket.device.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.records.RecordsScanMe;
import com.fancypants.rest.RestScanMe;
import com.fancypants.stream.StreamScanMe;
import com.fancypants.websocket.WebSocketScanMe;
import com.fancypants.websocket.device.WebSocketDeviceScanMe;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class,
		DeviceScanMe.class, RecordsScanMe.class, StreamScanMe.class,
		RestScanMe.class, WebSocketScanMe.class, WebSocketDeviceScanMe.class })
@EnableWebSocketMessageBroker
public class WebsocketDeviceWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CompositeMessageConverter messageConverter;
	
	@PostConstruct
	private void init() {
		// find the JSON converter
		for (MessageConverter converter : messageConverter.getConverters()) {
			if (true == converter instanceof MappingJackson2MessageConverter) {
				// override the object mapper
				((MappingJackson2MessageConverter)converter).setObjectMapper(objectMapper);
			}
		}
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp");
	}
}
