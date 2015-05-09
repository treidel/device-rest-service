package com.fancypants.websocket.device.listener;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component 
public class SessionDisconnectedListener implements
		ApplicationListener<SessionDisconnectEvent> {

	private static final Logger LOG = LoggerFactory
			.getLogger(SessionDisconnectedListener.class);

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		LOG.trace("SessionDisconnectedListener.onApplicationEvent enter event="
				+ event);
		// extract the stomp headers
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(event
				.getMessage());
		// get the user
		Principal user = headers.getUser();

		LOG.info("disconnection for device=" + user.getName());

		// TBD: remove from session database

		LOG.trace("SessionDisconnectedListener.onApplicationEvent exit");
	}

}
