package com.fancypants.rest.app.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.rest.app.resource.PowerConsumptionResource;
import com.fancypants.rest.app.web.DeviceController;
import com.fancypants.rest.app.web.PowerConsumptionController;
import com.fancypants.rest.domain.PowerConsumptionRecord;

@Component
public class PowerConsumptionResourceAssembler
		extends
		ResourceAssemblerSupport<PowerConsumptionRecord, PowerConsumptionResource> {

	public PowerConsumptionResourceAssembler() {
		super(PowerConsumptionController.class, PowerConsumptionResource.class);
	}

	@Override
	public PowerConsumptionResource toResource(PowerConsumptionRecord entity) {
		PowerConsumptionResource resource = new PowerConsumptionResource();
		resource.record = entity;
		resource.add(linkTo(methodOn(DeviceController.class).getDevice())
				.withSelfRel());

		return resource;
	}

}
