package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawMeasurement implements
		Comparable<RawMeasurement> {

	private final String circuit;
	private final float powerInKWh;

	@JsonCreator
	public RawMeasurement(@JsonProperty("circuit") String circuit,
			@JsonProperty("power-in-kwh") float powerInKWh) {
		this.circuit = circuit;
		this.powerInKWh = powerInKWh;
	}

	public String getCircuit() {
		return circuit;
	}

	public float getPowerInKWh() {
		return powerInKWh;
	}

	@Override
	public int compareTo(RawMeasurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
}
