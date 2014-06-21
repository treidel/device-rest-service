package com.test.rest.device.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.rest.device.assembler.RecordResourceAssembler;
import com.test.rest.device.domain.Measurement;
import com.test.rest.device.domain.Record;
import com.test.rest.device.resource.RecordResource;

@Controller
@RequestMapping("/device")
public class RecordController {

	private final RecordResourceAssembler recordResourceAssembler = new RecordResourceAssembler();
	private @Autowired ObjectMapper objectMapper;

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
			@RequestBody Collection<Record> records) {
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		for (Record record : records) {
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
	public HttpEntity<Collection<RecordResource>> getRecords() {
		Set<Measurement> measurements = new HashSet<Measurement>(32);
		Measurement measurement = new Measurement("1-0", 0.5f);
		measurements.add(measurement);
		Collection<RecordResource> resources = new LinkedList<RecordResource>();
		Record record = new Record(UUID.randomUUID(), new Date(), measurements);
		RecordResource resource = recordResourceAssembler.toResource(record);	
		resources.add(resource);
		return new ResponseEntity<Collection<RecordResource>>(resources,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/records/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<RecordResource> getRecord(
			@PathVariable("uuid") String uuid) {
		Set<Measurement> measurements = new HashSet<Measurement>(32);
		Measurement measurement = new Measurement("1-0", 0.5f);
		measurements.add(measurement);
		Record record = new Record(UUID.fromString(uuid), new Date(),
				measurements);
		RecordResource resource = recordResourceAssembler.toResource(record);
		return new ResponseEntity<RecordResource>(resource, HttpStatus.OK);
	}

}
