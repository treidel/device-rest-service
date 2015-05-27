package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.rest.device.controller.AdminController;
import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.domain.Device;

@Component
public class DeviceResourceAssembler extends
		ResourceAssemblerSupport<Device, DeviceResource> {

	private static final Logger LOG = LoggerFactory
			.getLogger(DeviceResourceAssembler.class);

	public DeviceResourceAssembler() {
		super(AdminController.class, DeviceResource.class);
	}

	@Override
	public DeviceResource toResource(Device entity) {
		DeviceResource resource = new DeviceResource();
		resource.device = entity;
		try {
			resource.add(linkTo(
					methodOn(AdminController.class).getDevice(entity.getName()))
					.withSelfRel());
		} catch (AbstractServiceException e) {
			LOG.error("exception received", e);
		}

		return resource;
	}

}
