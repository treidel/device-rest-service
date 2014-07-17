package com.fancypants.rest.domain;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerConsumptionRecord implements
		Comparable<PowerConsumptionRecord> {

	private final Date date;
	private final Set<PowerConsumptionMeasurement> measurements;

	@JsonCreator
	public PowerConsumptionRecord(@JsonProperty("date") Date date,
			@JsonProperty("value") Set<PowerConsumptionMeasurement> measurements) {
		this.date = date;
		this.measurements = measurements;
	}

	@Override
	public int compareTo(PowerConsumptionRecord record) {
		return this.date.compareTo(record.date);
	}

	public Date getDate() {
		return date;
	}

	public Set<PowerConsumptionMeasurement> getMeasurements() {
		return measurements;
	}

}
