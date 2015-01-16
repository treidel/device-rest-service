package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.rest.device.controller.RecordController;
import com.fancypants.rest.device.resource.RawRecordResource;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.domain.RawRecord;

@Component
public class CurrentRecordResourceAssembler extends
		ResourceAssemblerSupport<Pair<Device, RawRecord>, RawRecordResource> {

	private static final Logger LOG = Logger.getLogger(CurrentRecordResourceAssembler.class);
	
	public CurrentRecordResourceAssembler() {
		super(RecordController.class, RawRecordResource.class);
	}

	@Override
	public RawRecordResource toResource(Pair<Device, RawRecord> entity) {
		RawRecordResource resource = new RawRecordResource();
		resource.record = entity.getRight();
		try {
			resource.add(linkTo(
					methodOn(RecordController.class).getRecord(
							entity.getRight().getUUID().toString())).withSelfRel());
		} catch (AbstractServiceException e) {
			LOG.error("exception received" , e);
		}

		return resource;
	}

}
