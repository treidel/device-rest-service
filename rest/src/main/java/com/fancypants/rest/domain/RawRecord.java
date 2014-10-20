package com.fancypants.rest.domain;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecord {

	public static final String UUID_ATTRIBUTE = "uuid";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public static final String MEASUREMENTS_ATTRIBUTE = "measurements";

	private final UUID uuid;
	private final Date timestamp;
	private final Set<RawMeasurement> measurements;

	@JsonCreator
	public RawRecord(
			@JsonProperty(UUID_ATTRIBUTE) UUID uuid,
			@JsonProperty(TIMESTAMP_ATTRIBUTE) Date timestamp,
			@JsonProperty(MEASUREMENTS_ATTRIBUTE) Set<RawMeasurement> measurements) {
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.measurements = measurements;
	}

	@JsonProperty(UUID_ATTRIBUTE)
	public UUID getUUID() {
		return uuid;
	}

	@JsonProperty(TIMESTAMP_ATTRIBUTE)
	public Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty(MEASUREMENTS_ATTRIBUTE)
	public Set<RawMeasurement> getMeasurements() {
		return measurements;
	}
}
