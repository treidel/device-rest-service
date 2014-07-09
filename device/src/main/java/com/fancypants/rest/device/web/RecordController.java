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
import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.request.DeviceContainer;
import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.device.service.DeviceService;
import com.fancypants.rest.device.service.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/device")
@Secured("ROLE_USER")
public class RecordController {

	private final RecordResourceAssembler recordResourceAssembler = new RecordResourceAssembler();
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
			@RequestBody Collection<Record> records) {
		// create the list of resources to be returned
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		// bulk create the records
		recordService.bulkCreateRecords(records);
		// go through each record and create the resource
		for (Record record : records) {
			RecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, Record>(
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
		// create the list of resources
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		// query for all of the records
		Collection<Record> records = recordService.findAllRecordsForDevice();
		for (Record record : records) {
			// create a resource for each record
			RecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, Record>(
							deviceContainer.getDevice(), record));
			resources.add(resource);
		}
		return new ResponseEntity<Collection<RecordResource>>(resources,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/records/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<RecordResource> getRecord(
			@PathVariable("uuid") String uuid) {
		// find the record
		Record record = recordService
				.findRecordForDevice(UUID.fromString(uuid));
		if (null == record) {
			return new ResponseEntity<RecordResource>(HttpStatus.NOT_FOUND);
		}
		RecordResource resource = recordResourceAssembler
				.toResource(new ImmutablePair<Device, Record>(deviceContainer
						.getDevice(), record));
		return new ResponseEntity<RecordResource>(resource, HttpStatus.OK);
	}

}
