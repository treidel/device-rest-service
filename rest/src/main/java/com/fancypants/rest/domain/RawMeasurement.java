package com.fancypants.rest.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawMeasurement implements Comparable<RawMeasurement> {

	public final static String CIRCUIT_ATTRIBUTE = "circuit";
	public final static String VOLTAGE_ATTRIBUTE = "voltage-in-v";
	public final static String AMPERAGE_ATTRIBUTE = "amperage-in-a";
	public final static String DURATION_ATTRIBUTE = "duration-in-s";

	private final String circuit;
	private final float voltageInVolts;
	private final float amperageInAmps;
	private final float durationInSeconds;

	@JsonCreator
	public RawMeasurement(@JsonProperty(CIRCUIT_ATTRIBUTE) String circuit,
			@JsonProperty(VOLTAGE_ATTRIBUTE) float voltageInVolts,
			@JsonProperty(AMPERAGE_ATTRIBUTE) float amperageInAmps,
			@JsonProperty(DURATION_ATTRIBUTE) float durationInSeconds) {
		this.circuit = circuit;
		this.durationInSeconds = durationInSeconds;
		this.voltageInVolts = voltageInVolts;
		this.amperageInAmps = amperageInAmps;
	}

	@JsonProperty(CIRCUIT_ATTRIBUTE)
	public String getCircuit() {
		return circuit;
	}

	@JsonProperty(DURATION_ATTRIBUTE)
	public Float getDurationInSeconds() {
		return durationInSeconds;
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
