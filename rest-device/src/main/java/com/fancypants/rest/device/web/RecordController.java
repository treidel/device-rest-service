package com.fancypants.rest.device.web;

import java.text.DateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.device.service.DeviceService;
import com.fancypants.device.service.RecordService;
import com.fancypants.rest.device.assembler.CurrentRecordResourceAssembler;
import com.fancypants.rest.device.resource.CurrentRecordResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.domain.RawRecord;
import com.fancypants.rest.mapping.RawRecordMapper;
import com.fancypants.rest.mapping.DeviceMapper;
import com.fancypants.rest.mapping.RawRecordEntityMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/device")
@Secured("ROLE_USER")
public class RecordController {

	@Autowired
	private CurrentRecordResourceAssembler recordResourceAssembler;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private RecordService recordService;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DateFormat iso8601DateFormat;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private RawRecordEntityMapper recordEntityMapper;
	@Autowired
	private RawRecordMapper recordMapper;
	@Autowired
	private DeviceMapper deviceMapper;

	@PostConstruct
	public void init() {
		// tell Jackson to use the ISO8601 date format
		objectMapper.setDateFormat(iso8601DateFormat);
	}

	@RequestMapping(value = "/records", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Collection<CurrentRecordResource>> putRecords(
			@RequestBody Collection<RawRecord> records)
			throws AbstractServiceException {
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
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
		recordService.bulkCreateRecords(entities);
		// map the device
		Device device = deviceMapper.convert(deviceEntity);
		// create the list of resources to be returned
		Collection<CurrentRecordResource> resources = new LinkedList<CurrentRecordResource>();
		// go through each record and create the resources
		for (RawRecord record : records) {
			CurrentRecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, RawRecord>(
							device, record));
			resources.add(resource);
		}
		ResponseEntity<Collection<CurrentRecordResource>> response = new ResponseEntity<Collection<CurrentRecordResource>>(
				resources, HttpStatus.CREATED);
		return response;
	}

	@RequestMapping(value = "/records", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<Collection<CurrentRecordResource>> getRecords() {
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// map the device
		Device device = deviceMapper.convert(deviceEntity);
		// find all records
		Collection<RawRecordEntity> entities = recordService
				.findRecordsForDevice();
		// create the return list
		Collection<CurrentRecordResource> resources = new LinkedList<CurrentRecordResource>();
		for (RawRecordEntity entity : entities) {
			// map the record
			RawRecord record = recordMapper
					.convert(new ImmutablePair<DeviceEntity, RawRecordEntity>(
							deviceEntity, entity));
			// create the resource
			CurrentRecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, RawRecord>(
							device, record));
			resources.add(resource);
		}
		return new ResponseEntity<Collection<CurrentRecordResource>>(resources,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/records/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<CurrentRecordResource> getRecord(
			@PathVariable("uuid") String uuid) throws AbstractServiceException {
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// find the record
		RawRecordEntity entity = recordService.findRecordForDevice(UUID
				.fromString(uuid));
		if (null == entity) {
			return new ResponseEntity<CurrentRecordResource>(
					HttpStatus.NOT_FOUND);
		}
		// map the record
		RawRecord record = recordMapper
				.convert(new ImmutablePair<DeviceEntity, RawRecordEntity>(
						deviceEntity, entity));
		// map the device
		Device device = deviceMapper.convert(deviceEntity);
		// create the resource
		CurrentRecordResource resource = recordResourceAssembler
				.toResource(new ImmutablePair<Device, RawRecord>(device,
						record));
		return new ResponseEntity<CurrentRecordResource>(resource,
				HttpStatus.OK);
	}

}
