package com.fancypants.rest.device.domain;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Record {

	private final UUID uuid;
	private final Date timestamp;
	private final Set<Measurement> circuits;

	@JsonCreator
	public Record(@JsonProperty("uuid") UUID uuid,
			@JsonProperty("timestamp") Date timestamp,
			@JsonProperty("circuits") Set<Measurement> circuits) {
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.circuits = circuits;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public Set<Measurement> getCircuits() {
		return circuits;
	}

}
