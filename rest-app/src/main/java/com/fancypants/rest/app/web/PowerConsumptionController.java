package com.fancypants.rest.app.web;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.rest.app.assembler.PowerConsumptionResourceAssembler;
import com.fancypants.rest.app.resource.PowerConsumptionRecordResource;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fancypants.rest.mapping.PowerConsumptionRecordMapper;
import com.fancypants.usage.service.UsageService;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

@Controller
@RequestMapping("/app/usage")
@Secured("ROLE_USER")
public class PowerConsumptionController {
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private UsageService usageService;
	@Autowired
	private PowerConsumptionRecordMapper recordMapper;
	@Autowired
	private PowerConsumptionResourceAssembler assembler;

	@RequestMapping(value = "/hourly", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<PowerConsumptionRecordResource>> getHourlyData() {
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// query all records for this device
		List<PowerConsumptionRecordEntity> entities = usageService
				.getHourlyRecords(deviceEntity);
		// create resources
		List<PowerConsumptionRecordResource> resources = new ArrayList<PowerConsumptionRecordResource>(
				entities.size());
		for (PowerConsumptionRecordEntity entity : entities) {
			// map the record
			PowerConsumptionRecord record = recordMapper
					.convert(new ImmutablePair<DeviceEntity, PowerConsumptionRecordEntity>(
							deviceEntity, entity));
			// wrap as a resource
			PowerConsumptionRecordResource resource = assembler
					.toResource(record);
			resources.add(resource);
		}
		return new ResponseEntity<List<PowerConsumptionRecordResource>>(
				resources, HttpStatus.OK);
	}

	@RequestMapping(value = "/hourly/{date}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<PowerConsumptionRecordResource> getHourlyData(
			@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String dateAsString)
			throws AbstractServiceException {
		try {
			// parse the date
			Date date = ISO8601Utils.parse(dateAsString, new ParsePosition(0));
			// get the device
			DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
			// query for specific record for this device
			PowerConsumptionRecordEntity entity = usageService.getHourlyRecord(
					deviceEntity, date);
			// map the record
			PowerConsumptionRecord record = recordMapper
					.convert(new ImmutablePair<DeviceEntity, PowerConsumptionRecordEntity>(
							deviceEntity, entity));
			// wrap as a resource
			PowerConsumptionRecordResource resource = assembler
					.toResource(record);

			return new ResponseEntity<PowerConsumptionRecordResource>(resource,
					HttpStatus.OK);
		} catch (ParseException e) {
			throw new DataValidationException("invalid date=" + dateAsString);
		}
	}
}
