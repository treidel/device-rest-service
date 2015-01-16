package com.fancypants.rest.app.assembler;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.rest.app.controller.DeviceController;
import com.fancypants.rest.app.resource.DeviceResource;
import com.fancypants.rest.domain.Device;

@Component
public class DeviceResourceAssembler extends
		ResourceAssemblerSupport<Device, DeviceResource> {

	public DeviceResourceAssembler() {
		super(DeviceController.class, DeviceResource.class);
	}

	@Override
	public DeviceResource toResource(Device entity) {
		DeviceResource resource = new DeviceResource();
		resource.device = entity;
		return resource;
	}

}
