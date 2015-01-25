package com.fancypants.websocket.device.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.device.service.DeviceService;
import com.fancypants.records.service.RecordService;
import com.fancypants.rest.domain.ErrorMessage;
import com.fancypants.rest.domain.RawRecord;
import com.fancypants.rest.mapping.RawRecordEntityMapper;
import com.fancypants.websocket.container.SessionContainer;

@Controller
public class RecordController {

	private static final Logger LOG = Logger.getLogger(RecordController.class);

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private RecordService recordService;

	@Autowired
	private SessionContainer sessionContainer;

	@Autowired
	private RawRecordEntityMapper recordEntityMapper;

	@MessageMapping("/records")
	public void handleRecords(Principal user, List<RawRecord> records)
			throws AbstractServiceException {
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
		LOG.trace("RecordsController.handleRecords exit");
	}

	@MessageExceptionHandler
	public ErrorMessage handleException(AbstractServiceException exception) {
		LOG.error("RecordsController.handleException", exception);
		ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());
		return errorMessage;
	}
}
