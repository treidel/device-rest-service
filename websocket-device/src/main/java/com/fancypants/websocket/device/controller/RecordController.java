package com.fancypants.websocket.device.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.device.service.DeviceService;
import com.fancypants.records.service.RecordService;
import com.fancypants.rest.domain.ErrorMessage;
import com.fancypants.rest.domain.RawRecord;
import com.fancypants.rest.mapping.RawRecordEntityMapper;
import com.fancypants.websocket.container.SessionContainer;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class RecordController {

	private static final Logger LOG = Logger.getLogger(RecordController.class);

	@Autowired
	private MessageChannel clientOutboundChannel;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private RecordService recordService;

	@Autowired
	private SessionContainer sessionContainer;

	@Autowired
	private RawRecordEntityMapper recordEntityMapper;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MessageMapping("/records")
	public void handleRecords(StompHeaderAccessor request, Principal user,
			@Payload RawRecord[] records) throws AbstractServiceException {
		LOG.trace("RecordsController.handleRecords enter" + " records"
				+ records);

		// make sure they are registered
		if (false == sessionContainer.isRegistered()) {
			throw new BusinessLogicException("not registered");
		}

		// get the device
		DeviceEntity deviceEntity = deviceService.getDevice(user.getName());
		// map the records into the internal format
		Collection<RawRecordEntity> entities = new LinkedList<RawRecordEntity>();
		for (RawRecord record : records) {
			// map the record
			RawRecordEntity entity = recordEntityMapper
					.convert(new ImmutablePair<DeviceEntity, RawRecord>(
							deviceEntity, record));
			// add it
			entities.add(entity);
		}
		// bulk create the records
		recordService.bulkCreateRecords(deviceEntity, entities);
		// see if they want confirmation we got it + processed it
		if (null != request.getReceipt()) {
			// create the response
			StompHeaderAccessor response = StompHeaderAccessor
					.create(StompCommand.RECEIPT);
			response.setSessionId(request.getSessionId());
			response.setReceiptId(request.getReceipt());
			// send it
			Message<byte[]> message = MessageBuilder.createMessage(
					"".getBytes(), response.getMessageHeaders());
			clientOutboundChannel.send(message);
		}

		LOG.trace("RecordsController.handleRecords exit");
	}

	@MessageExceptionHandler
	public void handleException(StompHeaderAccessor request, AbstractServiceException exception) {
		LOG.error("RecordsController.handleException", exception);
		try {
		// create the error
		ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());
		// create the response
		StompHeaderAccessor response = StompHeaderAccessor
				.create(StompCommand.ERROR);
		response.setSessionId(request.getSessionId());
		response.setReceiptId(request.getReceipt());
		String messageText = objectMapper.writeValueAsString(errorMessage);
		response.setMessage(messageText);
		// send it
		Message<byte[]> message = MessageBuilder.createMessage(
				"".getBytes(), response.getMessageHeaders());
		clientOutboundChannel.send(message);
		} catch (JsonProcessingException e) {
			LOG.error(e);
		}
	}
}
