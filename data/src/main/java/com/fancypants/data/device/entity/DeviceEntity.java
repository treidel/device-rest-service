package com.fancypants.data.device.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
	public static final int MAX_CIRCUITS = 16;

	private final String device;
	private final String serialnumber;
	private final Set<CircuitEntity> circuits;
	private final Date lastModifiedTimestamp;
	private final Map<Integer, CircuitEntity> circuitLookupByIndex = new TreeMap<Integer, CircuitEntity>();
	private final Map<String, CircuitEntity> circuitLookupByName = new HashMap<String, CircuitEntity>();

	@JsonCreator
	public DeviceEntity(
			@JsonProperty(DEVICE_ATTRIBUTE) String device,
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

	@JsonProperty(DEVICE_ATTRIBUTE)
	public String getDevice() {
		return device;
	}

	@JsonProperty(SERIALNUMBER_ATTRIBUTE)
	public String getSerialNumber() {
		return serialnumber;
	}

	@JsonProperty(CIRCUITS_ATTRIBUTE)
	public Set<CircuitEntity> getCircuits() {
		return circuits;
	}

	@JsonProperty(LASTMODIFIEDTIMESTAMP_ATTRIBUTE)
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

	public static class TEST {
		public static final DeviceEntity DEVICEENTITY;

		static {
			// setup the circuits
			Set<CircuitEntity> circuits = new HashSet<CircuitEntity>();
			for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
				CircuitEntity circuit = new CircuitEntity(i, "1-" + i, 120.0f,
						10.0f);
				circuits.add(circuit);
			}
			// setup the device
			DEVICEENTITY = new DeviceEntity("ABCD1234", "000000001", circuits,
					new Date());
		}
	}
}
