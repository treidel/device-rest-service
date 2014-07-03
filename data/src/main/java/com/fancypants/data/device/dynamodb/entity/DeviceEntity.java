package com.fancypants.data.device.dynamodb.entity;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "devices")
public class DeviceEntity {

	private String device;
	private String serialnumber;
	private Set<Circuit> circuits;
	private String lastModifiedTimestamp;

	@DynamoDBHashKey(attributeName = "device")
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	@DynamoDBRangeKey
	public String getSerialNumber() {
		return serialnumber;
	}

	public void setSerialNumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}
	
	@DynamoDBAttribute
	@DynamoDBMarshalling(marshallerClass = CircuitMarshaller.class)
	public Set<Circuit> getCircuits() {
		return circuits;
	}
	
	public void setCircuits(Set<Circuit> circuits) {
		this.circuits = circuits;
	}

	@DynamoDBAttribute
	public String getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	public void setLastModifiedTimestamp(String lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}
	
}
