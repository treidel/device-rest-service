package com.fancypants.rest.device.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fancypants.rest.device.assembler.RecordResourceAssembler;
import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.device.service.RecordService;

@Controller
@RequestMapping("/device")
public class RecordController {

	private final RecordResourceAssembler recordResourceAssembler = new RecordResourceAssembler();
	private @Autowired
	ObjectMapper objectMapper;
	private @Autowired
	RecordService service;

	@PostConstruct
	public void init() {
		// turn on ISO8601/RFC3339 time format in Jackson
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		objectMapper.setDateFormat(df);
	}

	@RequestMapping(value = "/records", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Collection<RecordResource>> putRecords(
			@AuthenticationPrincipal User user,
			@RequestBody Collection<Record> records) {
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		for (Record record : records) {
			service.createOrUpdateRecord(user.getUsername(), record);
			RecordResource resource = recordResourceAssembler
					.toResource(record);
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
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		Collection<Record> records = service.findAllRecordsForDevice(user
				.getUsername());
		for (Record record : records) {
			RecordResource resource = recordResourceAssembler
					.toResource(record);
			resources.add(resource);
		}
		return new ResponseEntity<Collection<RecordResource>>(resources,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/records/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<RecordResource> getRecord(
			@AuthenticationPrincipal User user,
			@PathVariable("uuid") String uuid) {
		Record record = service.findRecordForDevice(user.getUsername(),
				UUID.fromString(uuid));
		if (null == record) {
			return new ResponseEntity<RecordResource>(HttpStatus.NOT_FOUND);
		}
		RecordResource resource = recordResourceAssembler.toResource(record);
		return new ResponseEntity<RecordResource>(resource, HttpStatus.OK);
	}

}
