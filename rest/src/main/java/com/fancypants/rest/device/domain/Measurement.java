package com.fancypants.rest.device.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Measurement implements
		Comparable<Measurement> {

	private final String circuit;
	private final float value;

	@JsonCreator
	public Measurement(@JsonProperty("circuit") String circuit,
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
	public int compareTo(Measurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
}
