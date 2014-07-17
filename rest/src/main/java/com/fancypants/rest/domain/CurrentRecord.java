package com.fancypants.rest.domain;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentRecord {

	private final UUID uuid;
	private final Date timestamp;
	private final Set<CurrentMeasurement> measurements;

	@JsonCreator
	public CurrentRecord(@JsonProperty("uuid") UUID uuid,
			@JsonProperty("timestamp") Date timestamp,
			@JsonProperty("measurements") Set<CurrentMeasurement> measurements) {
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.measurements = measurements;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public Set<CurrentMeasurement> getMeasurements() {
		return measurements;
	}

}
