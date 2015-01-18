package com.fancypants.websocket.app.controller;

import java.security.Principal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.fancypants.websocket.app.container.WebsocketSessionContainer;

@Controller
public class DeviceController {
	
	private static final Logger LOG = Logger.getLogger(DeviceController.class);
	
	@Autowired
	private WebsocketSessionContainer sessionContainer;
	
	@Autowired
	private SimpMessagingTemplate template; 

	@SubscribeMapping("/topic/device/notifications")
	public void handleNotificationSubscription(Principal user, Message<?> message) {
		LOG.trace("DeviceController.handleNotificationSubscription enter" + " user" + user + " message" + message);
		// only allowed to subscribe if they are registered
		if (false == sessionContainer.isRegistered()) {
			throw new IllegalAccessError("must be registered to subscribe to notifications");
		}
		// map subscriptions to the actual device topic
		this.template.convertAndSend("/topic/device." + user.getName(), message);
		LOG.trace("DeviceController.handleNotificationSubscription exit");
	}
}
