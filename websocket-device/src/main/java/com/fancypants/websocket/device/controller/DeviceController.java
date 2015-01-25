package com.fancypants.websocket.device.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.rest.domain.ErrorMessage;
import com.fancypants.websocket.container.SessionContainer;
import com.fancypants.websocket.device.domain.DeviceInfo;

@Controller
public class DeviceController {

	private static final Logger LOG = Logger.getLogger(DeviceController.class);

	@Autowired
	private SessionContainer sessionContainer;

	@MessageMapping("/registration")
	public void handleDeviceRegistration(DeviceInfo deviceInfo)
			throws AbstractServiceException {
		LOG.trace("DeviceController.handleDeviceRegistration enter" + " deviceInfo"
				+ deviceInfo);
		
		// TBD: log the device info
		
		// mark the session as registered
		sessionContainer.setRegistered(true);
		
		LOG.trace("RecordsController.handleRecords exit");
	}

	@MessageExceptionHandler
	public ErrorMessage handleException(AbstractServiceException exception) {
		LOG.error("RecordsController.handleException", exception);
		ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());
		return errorMessage;
	}
}
