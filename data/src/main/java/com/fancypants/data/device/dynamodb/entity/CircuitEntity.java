package com.fancypants.data.device.dynamodb.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CircuitEntity implements Comparable<CircuitEntity> {
	private int index;
	private String name;
	private Float voltage;
	private Float amperage;

	@JsonCreator
	public CircuitEntity(@JsonProperty("index") int index,
			@JsonProperty("name") String name,
			@JsonProperty("voltage") float voltage,
			@JsonProperty("amperage") float amperage) {
		this.index = index;
		this.name = name;
		this.voltage = voltage;
		this.amperage = amperage;
	}

	public Integer getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public Float getVoltage() {
		return voltage;
	}

	public Float getAmperage() {
		return amperage;
	}

	@Override
	public int compareTo(CircuitEntity circuit) {
		return Integer.compare(this.index, circuit.index);
	}
}
