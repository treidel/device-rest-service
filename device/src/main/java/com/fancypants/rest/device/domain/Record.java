package com.fancypants.rest.device.domain;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Record {

	private final UUID uuid;
	private final Date timestamp;
	private final Set<Measurement> measurements;

	@JsonCreator
	public Record(@JsonProperty("uuid") UUID uuid,
			@JsonProperty("timestamp") Date timestamp,
			@JsonProperty("measurements") Set<Measurement> measurements) {
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
	
	public Set<Measurement> getMeasurements() {
		return measurements;
	}

}
