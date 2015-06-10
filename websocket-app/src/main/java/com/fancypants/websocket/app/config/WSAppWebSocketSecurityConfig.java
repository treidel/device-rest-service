package com.fancypants.websocket.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WSAppWebSocketSecurityConfig extends
		AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(
			MessageSecurityMetadataSourceRegistry messages) {
		messages.simpDestMatchers("/topic/device/notifications")
				.hasRole("USER").anyMessage().hasRole("USER")
				.simpDestMatchers("/queue/**", "/topic/**").denyAll();
	}

	@Override
	protected boolean sameOriginDisabled() {
		// disable CSRF
		return true;
	}

}