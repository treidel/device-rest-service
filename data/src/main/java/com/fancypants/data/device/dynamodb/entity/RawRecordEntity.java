package com.fancypants.data.device.dynamodb.entity;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "raw")
public class RawRecordEntity {

	public static final String RECORDID_ATTRIBUTE = "recordId";
	public static final String DEVICE_ATTRIBUTE = "device";
	public static final String UUID_ATTRIBUTE = "uuid";
	public static final String DURATION_ATTRIBUTE = "duration";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public static final String CIRCUIT_ATTRIBUTE_PREFIX = "circuitAMPS";
	public static final String HASH_KEY = DEVICE_ATTRIBUTE;
	public static final String RANGE_KEY = UUID_ATTRIBUTE;
	public static final int MIN_CIRCUIT = 1;
	public static final int MAX_CIRCUIT = 8;

	private String timestamp;
	private Map<Integer, Float> circuits;
	private float duration;

	@Id
	@DynamoDBAttribute(attributeName = RECORDID_ATTRIBUTE)
	@DynamoDBMarshalling(marshallerClass = RawRecordIdMarshaller.class)
	private RawRecordId recordId;

	public RawRecordId getRecordId() {
		return recordId;
	}

	public void setRecordId(RawRecordId recordId) {
		this.recordId = recordId;
	}

	@DynamoDBHashKey(attributeName = HASH_KEY)
	public String getDevice() {
		return recordId != null ? recordId.getDevice() : null;
	}

	public void setDevice(String device) {
		if (recordId == null) {
			recordId = new RawRecordId();
		}
		this.recordId.setDevice(device);
	}

	@DynamoDBRangeKey(attributeName = RANGE_KEY)
	public String getUUID() {
		return recordId != null ? recordId.getUUID() : null;
	}

	public void setUUID(String uuid) {
		if (recordId == null) {
			recordId = new RawRecordId();
		}
		this.recordId.setUUID(uuid);

	}

	@DynamoDBAttribute(attributeName = TIMESTAMP_ATTRIBUTE)
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@DynamoDBAttribute(attributeName = DURATION_ATTRIBUTE)
	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "1")
	public float getCircuit1() {
		return getCircuit(1);
	}

	public void setCircuit1(float value) {
		setCircuit(1, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "2")
	public float getCircuit2() {
		return getCircuit(2);
	}

	public void setCircuit2(float value) {
		setCircuit(2, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "3")
	public float getCircuit3() {
		return getCircuit(3);
	}

	public void setCircuit3(float value) {
		setCircuit(3, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "4")
	public float getCircuit4() {
		return getCircuit(4);
	}

	public void setCircuit4(float value) {
		setCircuit(4, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "5")
	public float getCircuit5() {
		return getCircuit(5);
	}

	public void setCircuit5(float value) {
		setCircuit(5, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "6")
	public float getCircuit6() {
		return getCircuit(6);
	}

	public void setCircuit6(float value) {
		setCircuit(6, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "7")
	public float getCircuit7() {
		return getCircuit(7);
	}

	public void setCircuit7(float value) {
		setCircuit(7, value);
	}

	@DynamoDBAttribute(attributeName = CIRCUIT_ATTRIBUTE_PREFIX + "8")
	public float getCircuit8() {
		return getCircuit(8);
	}

	public void setCircuit8(float value) {
		setCircuit(8, value);
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
