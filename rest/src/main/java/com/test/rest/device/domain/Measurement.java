package com.test.rest.device.domain;

import org.springframework.hateoas.ResourceSupport;

public class Measurement extends ResourceSupport {

	String circuit;
	Float value;
}
