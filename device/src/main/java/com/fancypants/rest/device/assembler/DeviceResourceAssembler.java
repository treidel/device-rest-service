package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.fancypants.rest.device.resource.DeviceResource;
import com.fancypants.rest.device.web.DeviceController;
import com.fancypants.rest.domain.Device;

public class DeviceResourceAssembler extends
		ResourceAssemblerSupport<Device, DeviceResource> {

	public DeviceResourceAssembler() {
		super(DeviceController.class, DeviceResource.class);
	}

	@Override
	public DeviceResource toResource(Device entity) {
		DeviceResource resource = new DeviceResource();
		resource.device = entity;
		resource.add(linkTo(methodOn(DeviceController.class).getDevice())
				.withSelfRel());

		return resource;
	}

}
