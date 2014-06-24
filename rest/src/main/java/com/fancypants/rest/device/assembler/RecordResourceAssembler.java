package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.device.web.RecordController;

public class RecordResourceAssembler extends
		ResourceAssemblerSupport<Record, RecordResource> {

	public RecordResourceAssembler() {
		super(RecordController.class, RecordResource.class);
	}

	@Override
	public RecordResource toResource(Record entity) {
		RecordResource resource = new RecordResource();
		resource.record = entity;
		resource.add(linkTo(
				methodOn(RecordController.class).getRecord(null,
						entity.getUUID().toString())).withSelfRel());

		return resource;
	}

}
