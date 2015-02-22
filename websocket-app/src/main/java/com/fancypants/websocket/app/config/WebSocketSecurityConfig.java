package com.fancypants.websocket.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends
		AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(
			MessageSecurityMetadataSourceRegistry messages) {
		messages.antMatchers(SimpMessageType.MESSAGE, "/queue/**", "/topic/**")
				.denyAll()
				.antMatchers(SimpMessageType.SUBSCRIBE,
						"/topic/device/notifications").hasRole("USER")
				.anyMessage().hasRole("USER");
	}

	
}