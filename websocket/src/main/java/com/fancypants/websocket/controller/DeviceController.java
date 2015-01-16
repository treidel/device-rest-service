package com.fancypants.websocket.controller;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.fancypants.websocket.container.SessionContainer;

@Controller
public class DeviceController {
	
	private static final Logger LOG = Logger.getLogger(DeviceController.class);
	
	@Autowired
	private SessionContainer sessionContainer;
	
	@Autowired
	private SimpMessagingTemplate template; 

	@SubscribeMapping("/topic/device/notifications")
	public void handleNotificationSubscription(Principal user, Message<?> message) {
		LOG.trace("DeviceController.handleNotificationSubscription enter" + " user" + user + " message" + message);
		// map subscriptions to the actual device topic
		this.template.convertAndSend("/topic/device." + user.getName(), message);
		LOG.trace("DeviceController.handleNotificationSubscription exit");
	}
	
	@MessageMapping("/registration")
	public void handleRegistration(Principal user, String message) {
		LOG.trace("DeviceController.handleRegistration enter" + " user" + user + " message" + message);
		// now registered
		sessionContainer.setIdentified(true);
		LOG.trace("DeviceController.handleRegistration exit");
	}
}
