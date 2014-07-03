package com.fancypants.rest.device.web;

import java.text.DateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.rest.device.assembler.RecordResourceAssembler;
import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.device.service.DeviceService;
import com.fancypants.rest.device.service.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/device")
public class RecordController {

	private final RecordResourceAssembler recordResourceAssembler = new RecordResourceAssembler();
	private @Autowired
	ObjectMapper objectMapper;
	private @Autowired
	RecordService recordService;
	private @Autowired
	DeviceService deviceService;
	private @Autowired
	DateFormat iso8601DateFormat;

	@RequestMapping(value = "/records", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Collection<RecordResource>> putRecords(
			@AuthenticationPrincipal User user,
			@RequestBody Collection<Record> records) {
		// get the device
		Device device = deviceService.getDevice(user.getUsername());
		// create the list of resources to be returned
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		// bulk create the records
		recordService.bulkCreateRecords(device, records);
		// go through each record and create the resource
		for (Record record : records) {
			RecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, Record>(device,
							record));
			resources.add(resource);
		}
		ResponseEntity<Collection<RecordResource>> response = new ResponseEntity<Collection<RecordResource>>(
				resources, HttpStatus.CREATED);
		return response;
	}

	@RequestMapping(value = "/records", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<Collection<RecordResource>> getRecords(
			@AuthenticationPrincipal User user) {
		// get the device
		Device device = deviceService.getDevice(user.getUsername());
		// create the list of resources
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		// query for all of the records
		Collection<Record> records = recordService
				.findAllRecordsForDevice(device);
		for (Record record : records) {
			// create a resource for each record
			RecordResource resource = recordResourceAssembler
					.toResource(new ImmutablePair<Device, Record>(device,
							record));
			resources.add(resource);
		}
		return new ResponseEntity<Collection<RecordResource>>(resources,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/records/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<RecordResource> getRecord(
			@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("uuid") String uuid) {
		// get the device
		Device device = deviceService.getDevice(userDetails.getUsername());
		// find the record
		Record record = recordService.findRecordForDevice(device,
				UUID.fromString(uuid));
		if (null == record) {
			return new ResponseEntity<RecordResource>(HttpStatus.NOT_FOUND);
		}
		RecordResource resource = recordResourceAssembler
				.toResource(new ImmutablePair<Device, Record>(device, record));
		return new ResponseEntity<RecordResource>(resource, HttpStatus.OK);
	}

}
