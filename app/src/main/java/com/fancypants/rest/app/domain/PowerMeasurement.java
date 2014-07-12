package com.fancypants.rest.app.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerMeasurement implements
		Comparable<PowerMeasurement> {

	private final String circuit;
	private final float value;

	@JsonCreator
	public PowerMeasurement(@JsonProperty("circuit") String circuit,
			@JsonProperty("value") float value) {
		this.circuit = circuit;
		this.value = value;
	}

	public String getCircuit() {
		return circuit;
	}

	public float getValue() {
		return value;
	}

	@Override
	public int compareTo(PowerMeasurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
}
