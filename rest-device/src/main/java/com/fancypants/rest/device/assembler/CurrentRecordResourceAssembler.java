package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.rest.device.resource.CurrentRecordResource;
import com.fancypants.rest.device.web.RecordController;
import com.fancypants.rest.domain.Device;
import com.fancypants.rest.domain.RawRecord;

@Component
public class CurrentRecordResourceAssembler extends
		ResourceAssemblerSupport<Pair<Device, RawRecord>, CurrentRecordResource> {

	private static final Logger LOG = Logger.getLogger(CurrentRecordResourceAssembler.class);
	
	public CurrentRecordResourceAssembler() {
		super(RecordController.class, CurrentRecordResource.class);
	}

	@Override
	public CurrentRecordResource toResource(Pair<Device, RawRecord> entity) {
		CurrentRecordResource resource = new CurrentRecordResource();
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
