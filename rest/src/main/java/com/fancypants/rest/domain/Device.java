package com.fancypants.rest.domain;

import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

	public static final String NAME_ATTRIBUTE = "name";
	public static final String SERIALNUMBER_ATTRIBUTE = "serial-number";
	public static final String CIRCUITS_ATTRIBUTE = "circuits";

	private final String name;
	private final String serialNumber;
	private final SortedSet<Circuit> circuits;

	@JsonCreator
	public Device(@JsonProperty(NAME_ATTRIBUTE) String name,
			@JsonProperty(SERIALNUMBER_ATTRIBUTE) String serialNumber,
			@JsonProperty(CIRCUITS_ATTRIBUTE) SortedSet<Circuit> circuits) {
		this.name = name;
		this.serialNumber = serialNumber;
		this.circuits = circuits;
	}

	@JsonProperty(NAME_ATTRIBUTE)
	public String getName() {
		return name;
	}

	@JsonProperty(SERIALNUMBER_ATTRIBUTE)
	public String getSerialNumber() {
		return serialNumber;
	}

	@JsonProperty(CIRCUITS_ATTRIBUTE)
	public SortedSet<Circuit> getCircuits() {
		return circuits;
	}

	public static class TEST {
		public static final Device DEVICE;

		static {
			// setup the test records
			SortedSet<Circuit> circuits = new TreeSet<Circuit>();
			for (int i = 1; i <= 16; i++) {
				Circuit circuit = new Circuit(String.valueOf(i) + "-1", 120.0f,
						30.0f);
				circuits.add(circuit);
			}
			DEVICE = new Device("ABCD1234", "00000001", circuits);
		}

	}
}
