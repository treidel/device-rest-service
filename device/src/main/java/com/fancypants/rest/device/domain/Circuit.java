package com.fancypants.rest.device.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Circuit implements Comparable<Circuit> {

	private final String name;
	private final float voltage;
	private final float amperage;

	@JsonCreator
	public Circuit(@JsonProperty("name") String name,
			@JsonProperty("voltage") float voltage,
			@JsonProperty("amperage") float amperage) {
		this.name = name;
		this.voltage = voltage;
		this.amperage = amperage;
	}

	public String getName() {
		return name;
	}

	public float getVoltage() {
		return voltage;
	}

	public float getAmperage() {
		return amperage;
	}

	@Override
	public int compareTo(Circuit circuit) {
		return name.compareTo(circuit.name);
	}
}
