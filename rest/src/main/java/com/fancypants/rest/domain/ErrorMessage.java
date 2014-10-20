package com.fancypants.rest.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessage {

	public static final String ERRORS_ATTRIBUTE = "errors";

	private List<String> errors;

	public ErrorMessage() {
	}

	@JsonCreator
	public ErrorMessage(@JsonProperty(ERRORS_ATTRIBUTE) List<String> errors) {
		this.errors = errors;
	}

	public ErrorMessage(String error) {
		this(Collections.singletonList(error));
	}

	public ErrorMessage(String... errors) {
		this(Arrays.asList(errors));
	}

	@JsonProperty(ERRORS_ATTRIBUTE)
	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}
