package com.fancypants.rest.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fancypants.rest.app.assembler.PowerConsumptionResourceAssembler;
import com.fancypants.rest.app.resource.PowerConsumptionResource;
import com.fancypants.rest.app.service.UsageService;
import com.fancypants.rest.domain.PowerConsumptionRecord;

@Controller
@RequestMapping("/app/usage")
@Secured("ROLE_USER")
public class PowerConsumptionController {

	@Autowired
	private UsageService usageService;

	@Autowired
	private PowerConsumptionResourceAssembler assembler;

	@RequestMapping(value = "/hourly", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<PowerConsumptionResource>> getHourlyData() {
		// query all records for this device
		Set<PowerConsumptionRecord> powerRecords = usageService
				.getMonthlyRecords();
		// create resources
		List<PowerConsumptionResource> resources = new ArrayList<PowerConsumptionResource>(
				powerRecords.size());
		for (PowerConsumptionRecord powerRecord : powerRecords) {
			PowerConsumptionResource resource = assembler
					.toResource(powerRecord);
			resources.add(resource);
		}
		return new ResponseEntity<List<PowerConsumptionResource>>(resources,
				HttpStatus.OK);
	}
}
