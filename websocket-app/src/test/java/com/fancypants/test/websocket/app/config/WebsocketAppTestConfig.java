package com.fancypants.test.websocket.app.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.message.TestMessageScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { WebsocketAppTestConfig.class,
		TestDataScanMe.class, TestMessageScanMe.class })
public class WebsocketAppTestConfig {

	@Bean
	public WebSocketClient websocketClient() {
		JettyWebSocketClient client = new JettyWebSocketClient();
		client.start();
		return client;
	}
}
