package com.fancypants.rest.device.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fancypants.data.device.dynamodb.entity.Circuit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

	private final String name;
	private final String serialNumber;
	private final Date lastModifiedTimestamp;
	private final Set<Circuit> circuits;
	private final Map<String, Circuit> circuitLookupByName = new HashMap<String, Circuit>();
	private final Map<Integer, Circuit> circuitLookupByIndex = new TreeMap<Integer, Circuit>();

	@JsonCreator
	public Device(
			@JsonProperty("name") String name,
			@JsonProperty("serial-number") String serialNumber,
			@JsonProperty("last-modified-timestamp") Date lastModifiedTimestamp,
			@JsonProperty("circuits") Set<Circuit> circuits) {
		this.name = name;
		this.serialNumber = serialNumber;
		this.lastModifiedTimestamp = lastModifiedTimestamp;
		this.circuits = circuits;
		// populate the lookup maps
		for (Circuit circuit : circuits) {
			this.circuitLookupByName.put(circuit.getName(), circuit);
			this.circuitLookupByIndex.put(circuit.getIndex(), circuit);
		}
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

	public Set<Circuit> getCircuits() {
		return circuits;
	}

	public Circuit getCircuitByName(String name) {
		return this.circuitLookupByName.get(name);
	}
	
	public Circuit getCircuitByIndex(int index) {
		return this.circuitLookupByIndex.get(index);
	}

}
