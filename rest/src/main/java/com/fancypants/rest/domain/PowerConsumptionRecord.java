package com.fancypants.rest.domain;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerConsumptionRecord implements
		Comparable<PowerConsumptionRecord> {

	public static final String DATE_ATTRIBUTE = "date";
	public static final String MEASUREMENTS_ATTRIBUTE = "measurements";

	private final Date date;
	private final Set<PowerConsumptionMeasurement> measurements;

	@JsonCreator
	public PowerConsumptionRecord(
			@JsonProperty(DATE_ATTRIBUTE) Date date,
			@JsonProperty(MEASUREMENTS_ATTRIBUTE) Set<PowerConsumptionMeasurement> measurements) {
		this.date = date;
		this.measurements = measurements;
	}

	@Override
	public int compareTo(PowerConsumptionRecord record) {
		return this.date.compareTo(record.date);
	}

	@JsonProperty(DATE_ATTRIBUTE)
	public Date getDate() {
		return date;
	}

	@JsonProperty(MEASUREMENTS_ATTRIBUTE)
	public Set<PowerConsumptionMeasurement> getMeasurements() {
		return measurements;
	}

}
