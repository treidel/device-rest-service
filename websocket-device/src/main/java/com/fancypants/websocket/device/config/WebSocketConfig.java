package com.fancypants.websocket.device.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, DataScanMe.class,
		DeviceScanMe.class, RecordsScanMe.class, StreamScanMe.class,
		RestScanMe.class, WebSocketScanMe.class, WebSocketDeviceScanMe.class })
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp").withSockJS();
	}
}
