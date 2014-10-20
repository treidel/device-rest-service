package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawMeasurement implements Comparable<RawMeasurement> {

	public static final String CIRCUIT_ATTRIBUTE = "circuit";
	public static final String POWERINKWH_ATTRIBUTE = "power-in-kwh";

	private final String circuit;
	private final float powerInKWh;

	@JsonCreator
	public RawMeasurement(@JsonProperty(CIRCUIT_ATTRIBUTE) String circuit,
			@JsonProperty(POWERINKWH_ATTRIBUTE) float powerInKWh) {
		this.circuit = circuit;
		this.powerInKWh = powerInKWh;
	}

	@JsonProperty(CIRCUIT_ATTRIBUTE)
	public String getCircuit() {
		return circuit;
	}

	@JsonProperty(POWERINKWH_ATTRIBUTE)
	public float getPowerInKWh() {
		return powerInKWh;
	}

	@Override
	public int compareTo(RawMeasurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
	
	@Override
	public int hashCode() {
		return circuit.hashCode();
	}
}
