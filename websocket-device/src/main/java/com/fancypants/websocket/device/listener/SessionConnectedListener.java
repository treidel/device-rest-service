package com.fancypants.websocket.device.listener;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.fancypants.websocket.device.container.DeviceSessionContainer;

@Component
public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {
	
	private static final Logger LOG = Logger.getLogger(SessionConnectedListener.class);
	
	@Autowired
	private DeviceSessionContainer sessionContainer;

	@Override
	public void onApplicationEvent(SessionConnectedEvent event) {
		LOG.trace("SessionConnectedListener.onApplicationEvent enter event=" + event);
		// extract the stomp headers
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
		// get the user and cache it in the session container
		Principal user = headers.getUser();
		sessionContainer.setUser(user);
		// TBD: store in session database
		
		LOG.trace("SessionConnectedListener.onApplicationEvent exit");
	}
	
}
