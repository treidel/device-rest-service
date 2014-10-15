package com.fancypants.rest.device.resource;

import org.springframework.hateoas.ResourceSupport;

import com.fancypants.rest.domain.RawRecord;

public class CurrentRecordResource extends ResourceSupport {

	public RawRecord record;
}
