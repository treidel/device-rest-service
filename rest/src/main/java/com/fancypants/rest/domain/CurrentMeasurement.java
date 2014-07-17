package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentMeasurement implements
		Comparable<CurrentMeasurement> {

	private final String circuit;
	private final float value;

	@JsonCreator
	public CurrentMeasurement(@JsonProperty("circuit") String circuit,
			@JsonProperty("value") float value) {
		this.circuit = circuit;
		this.value = value;
	}

	public String getCircuit() {
		return circuit;
	}

	public float getCurrent() {
		return value;
	}

	@Override
	public int compareTo(CurrentMeasurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
}
