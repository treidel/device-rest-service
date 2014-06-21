package com.test.rest.device.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.rest.device.domain.Measurement;
import com.test.rest.device.domain.Record;

@Controller
@RequestMapping("/device")
public class RecordController {

	private @Autowired com.fasterxml.jackson.databind.ObjectMapper objectMapper;
	
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
    public void putRecords(Collection<Record> records) {
        for (Record record : records) {
        	System.err.println(record);
        }
    }
    

    @RequestMapping(value = "/records", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<Collection<Record>> getRecords() {
    	Collection<Record> records = new LinkedList<Record>();
    	Record record = new Record(UUID.randomUUID(), new Date(), new LinkedList<Measurement>());
    	records.add(record);
    	return new ResponseEntity<Collection<Record>>(records, HttpStatus.OK);
    }

}
