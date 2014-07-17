package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerConsumptionMeasurement implements
		Comparable<PowerConsumptionMeasurement> {

	private final String circuit;
	private float value;

	@JsonCreator
	public PowerConsumptionMeasurement(@JsonProperty("circuit") String circuit,
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

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public int compareTo(PowerConsumptionMeasurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
}
