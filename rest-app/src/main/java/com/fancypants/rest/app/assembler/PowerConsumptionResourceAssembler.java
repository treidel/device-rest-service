package com.fancypants.rest.app.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.rest.app.controller.PowerConsumptionController;
import com.fancypants.rest.app.resource.PowerConsumptionRecordResource;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

@Component
public class PowerConsumptionResourceAssembler
		extends
		ResourceAssemblerSupport<PowerConsumptionRecord, PowerConsumptionRecordResource> {
	private static final Logger LOG = LoggerFactory
			.getLogger(PowerConsumptionResourceAssembler.class);

	public PowerConsumptionResourceAssembler() {
		super(PowerConsumptionController.class,
				PowerConsumptionRecordResource.class);
	}

	@Override
	public PowerConsumptionRecordResource toResource(
			PowerConsumptionRecord entity) {
		PowerConsumptionRecordResource resource = new PowerConsumptionRecordResource();
		resource.record = entity;
		try {
			String dateAsString = ISO8601Utils.format(entity.getDate());
			resource.add(linkTo(
					methodOn(PowerConsumptionController.class).getHourlyData(
							dateAsString)).withSelfRel());
		} catch (AbstractServiceException e) {
			LOG.error("exception received", e);
		}
		return resource;
	}
}
