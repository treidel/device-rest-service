package com.fancypants.data.device.dynamodb.entity;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "records")
public class RecordEntity {

	public static final int MIN_CIRCUIT = 1;
	public static final int MAX_CIRCUIT = 8;

	private String timestamp;
	private Map<Integer, Float> circuits;

	@Id
	@DynamoDBMarshalling(marshallerClass = RecordIdMarshaller.class)
	private RecordId recordId;

	public RecordId getRecordId() {
		return recordId;
	}

	public void setRecordId(RecordId recordId) {
		this.recordId = recordId;
	}

	@DynamoDBHashKey(attributeName = "device")
	public String getDevice() {
		return recordId != null ? recordId.getDevice() : null;
	}

	public void setDevice(String device) {
		if (recordId == null) {
			recordId = new RecordId();
		}
		this.recordId.setDevice(device);
	}

	@DynamoDBRangeKey(attributeName = "uuid")
	public String getUUID() {
		return recordId != null ? recordId.getUUID() : null;
	}

	public void setUUID(String uuid) {
		if (recordId == null) {
			recordId = new RecordId();
		}
		this.recordId.setUUID(uuid);

	}

	@DynamoDBIndexRangeKey( attributeName = "timestamp", localSecondaryIndexName = "timestamp-index")
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@DynamoDBAttribute(attributeName = "circuit1")
	public Float getCircuit1() {
		return getCircuit(1);
	}

	public void setCircuit1(Float value) {
		setCircuit(1, value);
	}

	@DynamoDBAttribute(attributeName = "circuit2")
	public Float getCircuit2() {
		return getCircuit(2);
	}

	public void setCircuit2(Float value) {
		setCircuit(2, value);
	}

	@DynamoDBAttribute(attributeName = "circuit3")
	public Float getCircuit3() {
		return getCircuit(3);
	}

	public void setCircuit3(Float value) {
		setCircuit(3, value);
	}

	@DynamoDBAttribute(attributeName = "circuit4")
	public Float getCircuit4() {
		return getCircuit(4);
	}

	public void setCircuit4(Float value) {
		setCircuit(4, value);
	}

	@DynamoDBAttribute(attributeName = "circuit5")
	public Float getCircuit5() {
		return getCircuit(5);
	}

	public void setCircuit5(Float value) {
		setCircuit(5, value);
	}

	@DynamoDBAttribute(attributeName = "circuit6")
	public Float getCircuit6() {
		return getCircuit(6);
	}

	public void setCircuit7(Float value) {
		setCircuit(7, value);
	}

	@DynamoDBAttribute(attributeName = "circuit7")
	public Float getCircuit7() {
		return getCircuit(7);
	}

	public void setCircuit8(Float value) {
		setCircuit(8, value);
	}

	@DynamoDBAttribute(attributeName = "circuit8")
	public Float getCircuit8() {
		return getCircuit(8);
	}

	public Float getCircuit(int index) {
		if (null == this.circuits) {
			return null;
		}
		return this.circuits.get(index);
	}

	public void setCircuit(int index, float value) {
		if ((index < MIN_CIRCUIT) || (index > MAX_CIRCUIT)) {
			throw new IllegalArgumentException("index=" + index
					+ " out of range (" + MIN_CIRCUIT + "," + MAX_CIRCUIT);
		}
		if (null == this.circuits) {
			this.circuits = new TreeMap<Integer, Float>();
		}
		this.circuits.put(index, value);
	}

	@DynamoDBIgnore
	public Map<Integer, Float> getCircuits() {
		return circuits;
	}

	public void setCircuits(Map<Integer, Float> circuits) {
		int min = Collections.min(circuits.keySet());
		int max = Collections.max(circuits.keySet());
		if ((min < MIN_CIRCUIT) || (max > MAX_CIRCUIT)) {
			throw new IllegalArgumentException("min=" + min + " or max=" + max
					+ " out of range (" + MIN_CIRCUIT + "," + MAX_CIRCUIT);
		}
		this.circuits = circuits;
	}
}
