package com.fancypants.data.device.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawMeasurementEntity implements Comparable<RawMeasurementEntity> {
	public final static String INDEX_ATTRIBUTE = "index";
	public final static String VOLTAGE_ATTRIBUTE = "voltage-in-v";
	public final static String AMPERAGE_ATTRIBUTE = "amperage-in-a";

	private final int index;
	private final float voltageInVolts;
	private final float amperageInAmps;

	@JsonCreator
	public RawMeasurementEntity(@JsonProperty(INDEX_ATTRIBUTE) int index,
			@JsonProperty(VOLTAGE_ATTRIBUTE) float voltageInVolts,
			@JsonProperty(AMPERAGE_ATTRIBUTE) float amperageInAmps) {
		this.index = index;
		this.voltageInVolts = voltageInVolts;
		this.amperageInAmps = amperageInAmps;
	}

	@JsonProperty(INDEX_ATTRIBUTE)
	public Integer getIndex() {
		return index;
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
	public int compareTo(RawMeasurementEntity circuit) {
		return Integer.compare(this.index, circuit.index);
	}
}
