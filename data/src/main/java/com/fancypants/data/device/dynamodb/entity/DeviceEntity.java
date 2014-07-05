package com.fancypants.data.device.dynamodb.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "devices")
public class DeviceEntity {

	private String device;
	private String serialnumber;
	private Set<CircuitEntity> circuits;
	private String lastModifiedTimestamp;
	private final Map<Integer, CircuitEntity> circuitLookupByIndex = new TreeMap<Integer, CircuitEntity>();
	private final Map<String, CircuitEntity> circuitLookupByName = new HashMap<String, CircuitEntity>();

	@DynamoDBHashKey(attributeName = "device")
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	@DynamoDBAttribute(attributeName = "serial-number")
	public String getSerialNumber() {
		return serialnumber;
	}

	public void setSerialNumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	@DynamoDBAttribute(attributeName = "circuits")
	@DynamoDBMarshalling(marshallerClass = CircuitEntityMarshaller.class)
	public Set<CircuitEntity> getCircuits() {
		return circuits;
	}

	public void setCircuits(Set<CircuitEntity> circuits) {
		this.circuits = circuits;
		this.circuitLookupByIndex.clear();
		this.circuitLookupByName.clear();
		for (CircuitEntity circuit : circuits) {
			this.circuitLookupByIndex.put(circuit.getIndex(), circuit);
			this.circuitLookupByName.put(circuit.getName(), circuit);
		}
	}

	@DynamoDBAttribute(attributeName = "last-modified-timestamp")
	public String getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	public void setLastModifiedTimestamp(String lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}

	@DynamoDBIgnore
	public CircuitEntity getCircuitByIndex(int index) {
		return this.circuitLookupByIndex.get(index);
	}

	@DynamoDBIgnore
	public CircuitEntity getCircuitByName(String name) {
		return this.circuitLookupByName.get(name);
	}
}
