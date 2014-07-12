package com.fancypants.rest.domain;

import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

	private final String name;
	private final String serialNumber;
	private final SortedSet<Circuit> circuits;

	@JsonCreator
	public Device(@JsonProperty("name") String name,
			@JsonProperty("serial-number") String serialNumber,
			@JsonProperty("circuits") SortedSet<Circuit> circuits) {
		this.name = name;
		this.serialNumber = serialNumber;
		this.circuits = circuits;
	}

	public String getName() {
		return name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public SortedSet<Circuit> getCircuits() {
		return circuits;
	}
}
