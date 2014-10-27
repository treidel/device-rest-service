package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawMeasurement implements Comparable<RawMeasurement> {

	public final static String CIRCUIT_ATTRIBUTE = "circuit";
	public final static String VOLTAGE_ATTRIBUTE = "voltage-in-v";
	public final static String AMPERAGE_ATTRIBUTE = "amperage-in-a";

	private final String circuit;
	private final float voltageInVolts;
	private final float amperageInAmps;

	@JsonCreator
	public RawMeasurement(@JsonProperty(CIRCUIT_ATTRIBUTE) String circuit,
			@JsonProperty(VOLTAGE_ATTRIBUTE) float voltageInVolts,
			@JsonProperty(AMPERAGE_ATTRIBUTE) float amperageInAmps) {
		this.circuit = circuit;
		this.voltageInVolts = voltageInVolts;
		this.amperageInAmps = amperageInAmps;
	}

	@JsonProperty(CIRCUIT_ATTRIBUTE)
	public String getCircuit() {
		return circuit;
	}

	@JsonProperty(VOLTAGE_ATTRIBUTE)
	public Float getVoltageInVolts() {
		return voltageInVolts;
	}

	@JsonProperty(AMPERAGE_ATTRIBUTE)
	public Float getAmperageInAmps() {
		return amperageInAmps;
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
