package com.fancypants.websocket.app.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import com.fancypants.websocket.app.domain.ClientInfo;
import com.fancypants.websocket.container.SessionContainer;

@Controller
public class AppController {

	private static final Logger LOG = LoggerFactory
			.getLogger(AppController.class);

	@Autowired
	private SessionContainer sessionContainer;

	@MessageMapping("/registration")
	public Message<?> handleRegistration(StompHeaderAccessor request,
			Principal user, ClientInfo clientInfo) {
		LOG.trace("DeviceController.handleRegistration enter", "request",
				request, "user", user, "clientInfo", clientInfo);

		// TODO: only allowed to call once

		// now registered
		sessionContainer.setRegistered(true);

		// create the response
		StompHeaderAccessor response = StompHeaderAccessor.create(
				StompCommand.RECEIPT, request.toNativeHeaderMap());
		// receipt -> receipt-id
		response.setReceiptId(response.getReceipt());
		response.setReceipt(null);
		
		Message<byte[]> message = MessageBuilder.createMessage("".getBytes(),
				response.getMessageHeaders());

		LOG.trace("DeviceController.handleRegistration exit", "message",
				message);
		return message;
	}
}
