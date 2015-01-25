package com.fancypants.websocket.app.controller;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.fancypants.websocket.app.domain.ClientInfo;
import com.fancypants.websocket.container.SessionContainer;

@Controller
public class AppController {
	
	private static final Logger LOG = Logger.getLogger(AppController.class);
	
	@Autowired
	private SessionContainer sessionContainer;
		
	@MessageMapping("/registration")
	public void handleRegistration(Principal user, ClientInfo message) {
		LOG.trace("DeviceController.handleRegistration enter" + " user" + user + " message" + message);
		// now registered
		sessionContainer.setRegistered(true);
		LOG.trace("DeviceController.handleRegistration exit");
	}
}
