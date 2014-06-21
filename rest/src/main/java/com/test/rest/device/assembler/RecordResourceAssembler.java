package com.test.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.test.rest.device.domain.Record;
import com.test.rest.device.resource.RecordResource;
import com.test.rest.device.web.RecordController;

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
				methodOn(RecordController.class).getRecord(
						entity.getUUID().toString())).withSelfRel());

		return resource;
	}

}
