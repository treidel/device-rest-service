package com.fancypants.rest.app.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.rest.app.assembler.PowerConsumptionResourceAssembler;
import com.fancypants.rest.app.resource.PowerConsumptionRecordResource;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fancypants.rest.mapping.PowerConsumptionRecordMapper;
import com.fancypants.usage.service.UsageService;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

@RestController
@RequestMapping("/app/usage")
@Secured("ROLE_USER")
public class PowerConsumptionController {

	private static final Logger LOG = LoggerFactory.getLogger(PowerConsumptionController.class);

	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private UsageService usageService;
	@Autowired
	private PowerConsumptionRecordMapper recordMapper;
	@Autowired
	private PowerConsumptionResourceAssembler assembler;

	@RequestMapping(value = "/hourly", method = RequestMethod.GET)
	public Resources<PowerConsumptionRecordResource> getHourlyData() {
		LOG.trace("getHourlyData enter");
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// query all records for this device
		List<EnergyConsumptionRecordEntity> entities = usageService.getHourlyRecords(deviceEntity);
		// create resources
		List<PowerConsumptionRecordResource> resources = new ArrayList<PowerConsumptionRecordResource>(entities.size());
		for (EnergyConsumptionRecordEntity entity : entities) {
			// map the record
			PowerConsumptionRecord record = recordMapper
					.convert(new ImmutablePair<DeviceEntity, EnergyConsumptionRecordEntity>(deviceEntity, entity));
			// wrap as a resource
			PowerConsumptionRecordResource resource = assembler.toResource(record);
			resources.add(resource);
		}
		Resources<PowerConsumptionRecordResource> value = new Resources<>(resources,
				linkTo(methodOn(PowerConsumptionController.class).getHourlyData()).withRel("self"));
		LOG.trace("getHourlyData enter {}", value);
		return value;
	}

	@RequestMapping(value = "/hourly/{date}", method = RequestMethod.GET)
	public PowerConsumptionRecordResource getHourlyData(
			@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String dateAsString)
					throws AbstractServiceException {
		LOG.trace("getHourlyData enter {}={}", "dateAsString", dateAsString);
		try {
			// parse the date
			Date date = ISO8601Utils.parse(dateAsString, new ParsePosition(0));
			// get the device
			DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
			// query for specific record for this device
			EnergyConsumptionRecordEntity entity = usageService.getHourlyRecord(deviceEntity, date);
			// map the record
			PowerConsumptionRecord record = recordMapper
					.convert(new ImmutablePair<DeviceEntity, EnergyConsumptionRecordEntity>(deviceEntity, entity));
			// wrap as a resource
			PowerConsumptionRecordResource resource = assembler.toResource(record);
			LOG.trace("getHourlyData enter {}", resource);
			return resource;
		} catch (ParseException e) {
			throw new DataValidationException("invalid date=" + dateAsString);
		}
	}
}
