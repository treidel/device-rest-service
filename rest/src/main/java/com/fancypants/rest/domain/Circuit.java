package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Circuit implements Comparable<Circuit> {

	public static final String NAME_ATTRIBUTE = "name";
	public static final String VOLTAGE_ATTRIBUTE = "voltage";
	public static final String AMPERAGE_ATTRIBUTE = "amperage";

	private final String name;
	private final float voltage;
	private final float amperage;

	@JsonCreator
	public Circuit(
			@JsonProperty(value = NAME_ATTRIBUTE, required = true) String name,
			@JsonProperty(value = VOLTAGE_ATTRIBUTE, required = true) float voltage,
			@JsonProperty(value = AMPERAGE_ATTRIBUTE, required = true) float amperage) {
		this.name = name;
		this.voltage = voltage;
		this.amperage = amperage;
	}

	@JsonProperty(NAME_ATTRIBUTE)
	public String getName() {
		return name;
	}

	@JsonProperty(VOLTAGE_ATTRIBUTE)
	public float getVoltage() {
		return voltage;
	}

	@JsonProperty(AMPERAGE_ATTRIBUTE)
	public float getAmperage() {
		return amperage;
	}

	@Override
	public int compareTo(Circuit circuit) {
		return name.compareTo(circuit.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
