package com.fancypants.rest.device.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.resource.RecordResource;
import com.fancypants.rest.device.web.RecordController;

public class RecordResourceAssembler extends
		ResourceAssemblerSupport<Pair<Device, Record>, RecordResource> {

	private @Autowired
	UserDetailsService userDetailsService;

	public RecordResourceAssembler() {
		super(RecordController.class, RecordResource.class);
	}

	@Override
	public RecordResource toResource(Pair<Device, Record> entity) {
		// get the user
		UserDetails userDetails = userDetailsService.loadUserByUsername(entity
				.getLeft().getName());
		RecordResource resource = new RecordResource();
		resource.record = entity.getRight();
		resource.add(linkTo(
				methodOn(RecordController.class).getRecord(userDetails,
						entity.getRight().getUUID().toString())).withSelfRel());

		return resource;
	}

}
