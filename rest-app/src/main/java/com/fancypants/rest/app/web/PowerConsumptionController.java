package com.fancypants.rest.app.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.device.service.UsageService;
import com.fancypants.rest.app.assembler.PowerConsumptionResourceAssembler;
import com.fancypants.rest.app.resource.PowerConsumptionResource;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fancypants.rest.mapping.PowerConsumptionRecordMapper;

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
	public HttpEntity<List<PowerConsumptionResource>> getHourlyData() {
		// get the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// query all records for this device
		List<PowerConsumptionRecordEntity> entities = usageService
				.getHourlyRecords();
		// create resources
		List<PowerConsumptionResource> resources = new ArrayList<PowerConsumptionResource>(
				entities.size());
		for (PowerConsumptionRecordEntity entity : entities) {
			// map the record
			PowerConsumptionRecord record = recordMapper
					.convert(new ImmutablePair<DeviceEntity, PowerConsumptionRecordEntity>(
							deviceEntity, entity));
			// wrap as a resource
			PowerConsumptionResource resource = assembler.toResource(record);
			resources.add(resource);
		}
		return new ResponseEntity<List<PowerConsumptionResource>>(resources,
				HttpStatus.OK);
	}
}
