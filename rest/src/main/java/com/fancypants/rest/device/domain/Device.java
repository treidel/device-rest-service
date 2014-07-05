package com.fancypants.rest.device.domain;

import java.util.Date;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

	private final String name;
	private final String serialNumber;
	private Date lastModifiedTimestamp;
	private final SortedSet<Circuit> circuits;

	@JsonCreator
	public Device(
			@JsonProperty("name") String name,
			@JsonProperty("serial-number") String serialNumber,
			@JsonProperty("last-modified-timestamp") Date lastModifiedTimestamp,
			@JsonProperty("circuits") SortedSet<Circuit> circuits) {
		this.name = name;
		this.serialNumber = serialNumber;
		this.lastModifiedTimestamp = lastModifiedTimestamp;
		this.circuits = circuits;
	}

	public String getName() {
		return name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}
	
	public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}

	public SortedSet<Circuit> getCircuits() {
		return circuits;
	}
}
