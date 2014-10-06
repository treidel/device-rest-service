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

import com.fancypants.rest.device.assembler.RecordResourceAssembler;
import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.domain.CurrentRecord;
import com.fancypants.rest.exception.AbstractServiceException;
import com.fancypants.rest.request.DeviceContainer;
import com.fancypants.rest.service.DeviceService;
import com.fancypants.rest.service.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/device")
@Secured("ROLE_USER")
public class RecordController {

	@Autowired
	private RecordResourceAssembler recordResourceAssembler;
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

	@PostConstruct
	public void init() {
		// tell Jackson to use the ISO8601 date format
		objectMapper.setDateFormat(iso8601DateFormat);
	}

	@RequestMapping(value = "/records", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Collection<RecordResource>> putRecords(
			@RequestBody Collection<CurrentRecord> records)
			throws AbstractServiceException {
		// create the list of resources to be returned
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		// bulk create the records
		recordService.bulkCreateRecords(records);
		// go through each record and create the resource
		for (CurrentRecord record : records) {
			RecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, CurrentRecord>(
							deviceContainer.getDevice(), record));
			resources.add(resource);
		}
		ResponseEntity<Collection<RecordResource>> response = new ResponseEntity<Collection<RecordResource>>(
				resources, HttpStatus.CREATED);
		return response;
	}

	@RequestMapping(value = "/records", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<Collection<RecordResource>> getRecords() {
		// find all records
		Collection<CurrentRecord> records = recordService
				.findRecordsForDevice();
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		for (CurrentRecord record : records) {
			RecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, CurrentRecord>(
							deviceContainer.getDevice(), record));
			resources.add(resource);
		}
		return new ResponseEntity<Collection<RecordResource>>(resources,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/records/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<RecordResource> getRecord(
			@PathVariable("uuid") String uuid) throws AbstractServiceException {
		// find the record
		CurrentRecord record = recordService.findRecordForDevice(UUID
				.fromString(uuid));
		if (null == record) {
			return new ResponseEntity<RecordResource>(HttpStatus.NOT_FOUND);
		}
		RecordResource resource = recordResourceAssembler
				.toResource(new ImmutablePair<Device, CurrentRecord>(
						deviceContainer.getDevice(), record));
		return new ResponseEntity<RecordResource>(resource, HttpStatus.OK);
	}

}
