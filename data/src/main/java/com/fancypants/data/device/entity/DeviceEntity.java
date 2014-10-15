package com.fancypants.data.device.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceEntity {
	public static final String TABLE_NAME = "devices";
	public static final String DEVICE_ATTRIBUTE = "device";
	public static final String SERIALNUMBER_ATTRIBUTE = "serial-number";
	public static final String CIRCUITS_ATTRIBUTE = "circuits";
	public static final String LASTMODIFIEDTIMESTAMP_ATTRIBUTE = "last-modified-timestamp";
	public static final String HASH_ATTRIBUTE = DEVICE_ATTRIBUTE;
	public static final int MAX_CIRCUITS = 8;

	private final String device;
	private final String serialnumber;
	private final Set<CircuitEntity> circuits;
	private final Date lastModifiedTimestamp;
	private final Map<Integer, CircuitEntity> circuitLookupByIndex = new TreeMap<Integer, CircuitEntity>();
	private final Map<String, CircuitEntity> circuitLookupByName = new HashMap<String, CircuitEntity>();

	@JsonCreator
	public DeviceEntity(@JsonProperty(DEVICE_ATTRIBUTE) String device,
			@JsonProperty(SERIALNUMBER_ATTRIBUTE) String serialnumber,
			@JsonProperty(CIRCUITS_ATTRIBUTE) Set<CircuitEntity> circuits,
			@JsonProperty(LASTMODIFIEDTIMESTAMP_ATTRIBUTE) Date lastModifiedTimestamp) {
		this.device = device;
		this.serialnumber = serialnumber;
		this.circuits = circuits;
		this.lastModifiedTimestamp = lastModifiedTimestamp;
		for (CircuitEntity circuit : circuits) {
			this.circuitLookupByIndex.put(circuit.getIndex(), circuit);
			this.circuitLookupByName.put(circuit.getName(), circuit);
		}
	}

	public String getDevice() {
		return device;
	}

	public String getSerialNumber() {
		return serialnumber;
	}

	public Set<CircuitEntity> getCircuits() {
		return circuits;
	}

	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	@JsonIgnore
	public CircuitEntity getCircuitByIndex(int index) {
		return this.circuitLookupByIndex.get(index);
	}

	@JsonIgnore
	public CircuitEntity getCircuitByName(String name) {
		return this.circuitLookupByName.get(name);
	}
}
