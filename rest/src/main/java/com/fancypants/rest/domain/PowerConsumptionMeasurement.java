package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerConsumptionMeasurement implements
		Comparable<PowerConsumptionMeasurement> {

	public static final String CIRCUIT_ATTRIBUTE = "circuit";
	public static final String POWERINKWH_ATTRIBUTE = "power-in-kwh";
	
	private final String circuit;
	private float powerInKWH;

	@JsonCreator
	public PowerConsumptionMeasurement(@JsonProperty(CIRCUIT_ATTRIBUTE) String circuit,
			@JsonProperty(POWERINKWH_ATTRIBUTE) float powerInKWH) {
		this.circuit = circuit;
		this.powerInKWH = powerInKWH;
	}

	@JsonProperty(CIRCUIT_ATTRIBUTE)
	public String getCircuit() {
		return circuit;
	}

	@JsonProperty(POWERINKWH_ATTRIBUTE)
	public float getPowerInKWH() {
		return powerInKWH;
	}

	public void setPowerInKWH(float powerInKWH) {
		this.powerInKWH = powerInKWH;
	}

	@Override
	public int compareTo(PowerConsumptionMeasurement measurement) {
		return circuit.compareTo(measurement.circuit);
	}
	
	@Override
	public int hashCode() {
		return circuit.hashCode();
	}
}
