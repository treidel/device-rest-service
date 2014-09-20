package com.fancypants.data.device.dynamodb.entity;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = PowerConsumptionRecordEntity.TABLE_NAME)
public class PowerConsumptionRecordEntity {

	public static final String TABLE_NAME = "monthly";
	public static final String HASH_KEY = "device";
	public static final String RANGE_KEY = "date";
	public static final String MEASUREMENT_ATTRIBUTE_PREFIX = "measurement";

	private String device;
	private String date;
	private Map<Integer, Float> measurements;

	@DynamoDBHashKey(attributeName = HASH_KEY)
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	@DynamoDBRangeKey(attributeName = RANGE_KEY)
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "1")
	public Float getMeasurement1() {
		return getMeasurement(1);
	}

	public void setMeasurement1(Float value) {
		setMeasurement(1, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "2")
	public Float getMeasurement2() {
		return getMeasurement(2);
	}

	public void setMeasurement2(Float value) {
		setMeasurement(2, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "3")
	public Float getMeasurement3() {
		return getMeasurement(3);
	}

	public void setMeasurement3(Float value) {
		setMeasurement(3, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "4")
	public Float getMeasurement4() {
		return getMeasurement(4);
	}

	public void setMeasurement4(Float value) {
		setMeasurement(4, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "5")
	public Float getMeasurement5() {
		return getMeasurement(5);
	}

	public void setMeasurement5(Float value) {
		setMeasurement(5, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "6")
	public Float getMeasurement6() {
		return getMeasurement(6);
	}

	public void setMeasurement6(Float value) {
		setMeasurement(6, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "7")
	public Float getMeasurement7() {
		return getMeasurement(7);
	}

	public void setMeasurement7(Float value) {
		setMeasurement(1, value);
	}

	@DynamoDBAttribute(attributeName = MEASUREMENT_ATTRIBUTE_PREFIX + "8")
	public Float getMeasurement8() {
		return getMeasurement(8);
	}

	public void setMeasurement8(Float value) {
		setMeasurement(8, value);
	}

	public Float getMeasurement(int index) {
		if (null == this.measurements) {
			return null;
		}
		return this.measurements.get(index);
	}

	public void setMeasurement(int index, float value) {
		if ((index < RawRecordEntity.MIN_CIRCUIT)
				|| (index > RawRecordEntity.MAX_CIRCUIT)) {
			throw new IllegalArgumentException("index=" + index
					+ " out of range (" + RawRecordEntity.MIN_CIRCUIT + ","
					+ RawRecordEntity.MAX_CIRCUIT);
		}
		if (null == this.measurements) {
			this.measurements = new TreeMap<Integer, Float>();
		}
		this.measurements.put(index, value);
	}

	@DynamoDBIgnore
	public Map<Integer, Float> getMeasurements() {
		return measurements;
	}

	public void setCircuits(Map<Integer, Float> measurements) {
		int min = Collections.min(measurements.keySet());
		int max = Collections.max(measurements.keySet());
		if ((min < RawRecordEntity.MIN_CIRCUIT)
				|| (max > RawRecordEntity.MAX_CIRCUIT)) {
			throw new IllegalArgumentException("min=" + min + " or max=" + max
					+ " out of range (" + RawRecordEntity.MIN_CIRCUIT + ","
					+ RawRecordEntity.MAX_CIRCUIT);
		}
		this.measurements = measurements;
	}
}
