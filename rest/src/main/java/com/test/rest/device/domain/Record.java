package com.test.rest.device.domain;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Record extends ResourceSupport {

	private final UUID uuid;
	private final Date timestamp;
	private final Collection<Measurement> circuits;

	@JsonCreator
	public Record(@JsonProperty("uuid") UUID uuid,
			@JsonProperty("timestamp") Date timestamp,
			@JsonProperty("circuits") Collection<Measurement> circuits) {
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
	
	public Collection<Measurement> getCircuits() {
		return circuits;
	}

}
