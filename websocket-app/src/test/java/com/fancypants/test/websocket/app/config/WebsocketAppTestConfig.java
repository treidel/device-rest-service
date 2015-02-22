package com.fancypants.test.websocket.app.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.message.TestMessageScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { WebsocketAppTestConfig.class,
		TestDataScanMe.class, TestMessageScanMe.class })
public class WebsocketAppTestConfig {

	@Bean
	public SockJsClient sockjsClient() {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		SockJsClient sockJsClient = new SockJsClient(transports);
		return sockJsClient;
	}
}
