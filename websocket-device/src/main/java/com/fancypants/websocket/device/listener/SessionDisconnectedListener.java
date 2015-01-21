package com.fancypants.websocket.device.listener;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectedListener implements
		ApplicationListener<SessionDisconnectEvent> {

	private static final Logger LOG = Logger
			.getLogger(SessionDisconnectedListener.class);

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		LOG.trace("SessionDisconnectedListener.onApplicationEvent enter event="
				+ event);
		// extract the stomp headers
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(event
				.getMessage());
		// get the user and cache it in the session container
		Principal user = headers.getUser();
		// TBD: remove from session database
		if (user != null)
			;
		LOG.trace("SessionDisconnectedListener.onApplicationEvent exit");
	}

}
