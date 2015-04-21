package com.fancypants.websocket.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends
		AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(
			MessageSecurityMetadataSourceRegistry messages) {
		messages.simpDestMatchers("/queue/**", "/topic/**").denyAll()
				.simpDestMatchers("/topic/device/notifications")
				.hasRole("USER").anyMessage().hasRole("USER");
	}

}