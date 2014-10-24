package com.fancypants.data.device.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CircuitEntity implements Comparable<CircuitEntity> {
	public final static String INDEX_ATTRIBUTE = "index";
	public final static String VOLTAGE_ATTRIBUTE = "voltage-in-v";
	public final static String AMPERAGE_ATTRIBUTE = "amperage-in-a";
	public final static String NAME_ATTRIBUTE = "name";

	private final int index;
	private final String name;
	private final float voltage;
	private final float amperage;

	@JsonCreator
	public CircuitEntity(@JsonProperty(INDEX_ATTRIBUTE) int index,
			@JsonProperty(NAME_ATTRIBUTE) String name,
			@JsonProperty(VOLTAGE_ATTRIBUTE) float voltage,
			@JsonProperty(AMPERAGE_ATTRIBUTE) float amperage) {
		this.index = index;
		this.name = name;
		this.voltage = voltage;
		this.amperage = amperage;
	}

	@JsonProperty(INDEX_ATTRIBUTE)
	public Integer getIndex() {
		return index;
	}

	@JsonProperty(NAME_ATTRIBUTE)
	public String getName() {
		return name;
	}

	@JsonProperty(VOLTAGE_ATTRIBUTE)
	public Float getVoltage() {
		return voltage;
	}

	@JsonProperty(AMPERAGE_ATTRIBUTE)
	public Float getAmperage() {
		return amperage;
	}

	@Override
	public int compareTo(CircuitEntity circuit) {
		return Integer.compare(this.index, circuit.index);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
