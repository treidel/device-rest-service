package com.fancypants.websocket.device.listener;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SessionConnectedListener.class);

	@Override
	public void onApplicationEvent(SessionConnectedEvent event) {
		LOG.trace("SessionConnectedListener.onApplicationEvent enter event=" + event);
		// extract the stomp headers
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
		// get the user and cache it in the session container
		Principal user = headers.getUser();

		LOG.info("connection received from device=" + user.getName());

		// TBD: store in session database

		LOG.trace("SessionConnectedListener.onApplicationEvent exit");
	}

}
