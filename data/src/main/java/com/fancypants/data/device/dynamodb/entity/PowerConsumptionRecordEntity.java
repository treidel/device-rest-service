package com.fancypants.data.device.dynamodb.entity;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = PowerConsumptionRecordEntity.TABLE_NAME)
public class PowerConsumptionRecordEntity {

	public static final String TABLE_NAME = "hourly";
	public static final String HASH_KEY = "device";
	public static final String RANGE_KEY = "date";
	public static final String ENERGY_ATTRIBUTE_PREFIX = "energyKWH";

	private PowerConsumptionRecordId recordId;
	private Map<Integer, Float> energy;

	@Id
	@DynamoDBIgnore
	public PowerConsumptionRecordId getRecordId() {
		return recordId;
	}

	public void setRecordId(PowerConsumptionRecordId recordId) {
		this.recordId = recordId;
	}

	@DynamoDBHashKey(attributeName = HASH_KEY)
	public String getDevice() {
		return recordId != null ? recordId.getDevice() : null;
	}

	public void setDevice(String device) {
		if (recordId == null) {
			recordId = new PowerConsumptionRecordId();
		}
		this.recordId.setDevice(device);
	}

	@DynamoDBRangeKey(attributeName = RANGE_KEY)
	public String getDate() {
		return recordId != null ? recordId.getDate() : null;
	}

	public void setDate(String date) {
		if (recordId == null) {
			recordId = new PowerConsumptionRecordId();
		}
		this.recordId.setDate(date);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "1")
	public float getEnergy1() {
		return getEnergy(1);
	}

	public void setEnergy1(float energyInKWH) {
		setEnergy(1, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "2")
	public float getEnergy2() {
		return getEnergy(2);
	}

	public void setEnergy2(float energyInKWH) {
		setEnergy(2, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "3")
	public float getEnergy3() {
		return getEnergy(3);
	}

	public void setEnergy3(float energyInKWH) {
		setEnergy(3, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "4")
	public float getEnergy4() {
		return getEnergy(4);
	}

	public void setEnergy4(float energyInKWH) {
		setEnergy(4, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "5")
	public float getEnergy5() {
		return getEnergy(5);
	}

	public void setEnergy5(float energyInKWH) {
		setEnergy(5, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "6")
	public float getEnergy6() {
		return getEnergy(6);
	}

	public void setEnergy6(float energyInKWH) {
		setEnergy(6, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "7")
	public float getEnergy7() {
		return getEnergy(7);
	}

	public void setEnergy7(float energyInKWH) {
		setEnergy(1, energyInKWH);
	}

	@DynamoDBAttribute(attributeName = ENERGY_ATTRIBUTE_PREFIX + "8")
	public float getEnergy8() {
		return getEnergy(8);
	}

	public void setEnergy8(float energyInKWH) {
		setEnergy(8, energyInKWH);
	}

	public Float getEnergy(int index) {
		if (null == this.energy) {
			return null;
		}
		return this.energy.get(index);
	}

	public void setEnergy(int index, float energyInKWH) {
		if ((index < RawRecordEntity.MIN_CIRCUIT)
				|| (index > RawRecordEntity.MAX_CIRCUIT)) {
			throw new IllegalArgumentException("index=" + index
					+ " out of range (" + RawRecordEntity.MIN_CIRCUIT + ","
					+ RawRecordEntity.MAX_CIRCUIT);
		}
		if (null == this.energy) {
			this.energy = new TreeMap<Integer, Float>();
		}
		this.energy.put(index, energyInKWH);
	}

	@DynamoDBIgnore
	public Map<Integer, Float> getEnergy() {
		return energy;
	}

	public void setCircuits(Map<Integer, Float> energy) {
		int min = Collections.min(energy.keySet());
		int max = Collections.max(energy.keySet());
		if ((min < RawRecordEntity.MIN_CIRCUIT)
				|| (max > RawRecordEntity.MAX_CIRCUIT)) {
			throw new IllegalArgumentException("min=" + min + " or max=" + max
					+ " out of range (" + RawRecordEntity.MIN_CIRCUIT + ","
					+ RawRecordEntity.MAX_CIRCUIT);
		}
		this.energy = energy;
	}
}
