package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.device.web.RecordController;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.domain.CurrentRecord;

@Component
public class RecordResourceAssembler extends
		ResourceAssemblerSupport<Pair<Device, CurrentRecord>, RecordResource> {

	public RecordResourceAssembler() {
		super(RecordController.class, RecordResource.class);
	}

	@Override
	public RecordResource toResource(Pair<Device, CurrentRecord> entity) {
		RecordResource resource = new RecordResource();
		resource.record = entity.getRight();
		resource.add(linkTo(
				methodOn(RecordController.class).getRecord(
						entity.getRight().getUUID().toString())).withSelfRel());

		return resource;
	}

}
