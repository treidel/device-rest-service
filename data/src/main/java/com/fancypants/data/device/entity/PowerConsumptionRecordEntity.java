package com.fancypants.data.device.entity;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerConsumptionRecordEntity {
	public static final String TABLE_NAME = "hourly";
	public static final String DEVICE_ATTRIBUTE = "device";
	public static final String DATE_ATTRIBUTE = "date";
	public static final String ENERGY_IN_KWH_ATTRIBUTE_PREFIX = "energy-in-kwh-";
	public static final String HASH_KEY = DEVICE_ATTRIBUTE;
	public static final String RANGE_KEY = DATE_ATTRIBUTE;

	private final PowerConsumptionRecordId id;
	private final Map<Integer, Float> energyMap;

	@JsonCreator
	public PowerConsumptionRecordEntity(
			@JsonProperty(DEVICE_ATTRIBUTE) String device,
			@JsonProperty(DATE_ATTRIBUTE) Date date) {
		this.id = new PowerConsumptionRecordId(device, date);
		this.energyMap = new TreeMap<Integer, Float>();
	}

	public PowerConsumptionRecordEntity(PowerConsumptionRecordId id,
			Map<Integer, Float> energy) {
		this.id = id;
		this.energyMap = energy;
	}

	public PowerConsumptionRecordEntity(PowerConsumptionRecordId id) {
		this.id = id;
		this.energyMap = new TreeMap<Integer, Float>();
	}

	@Id
	@JsonIgnore
	public PowerConsumptionRecordId getId() {
		return id;
	}

	public String getDevice() {
		return id.getDevice();
	}

	public Date getDate() {
		return id.getDate();
	}

	@JsonIgnore
	public Float getEnergy(int index) {
		if (null == this.energyMap) {
			return null;
		}
		return this.energyMap.get(index);
	}

	@JsonIgnore
	public void setEnergy(int index, float energyInKWH) {
		this.energyMap.put(index, energyInKWH);
	}

	@JsonIgnore
	public Map<Integer, Float> getEnergy() {
		return energyMap;
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "1")
	public float getEnergy1() {
		return getEnergy(1);
	}

	public void setEnergy1(float energyInKWH) {
		setEnergy(1, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "2")
	public float getEnergy2() {
		return getEnergy(2);
	}

	public void setEnergy2(float energyInKWH) {
		setEnergy(2, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "3")
	public float getEnergy3() {
		return getEnergy(3);
	}

	public void setEnergy3(float energyInKWH) {
		setEnergy(3, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "4")
	public float getEnergy4() {
		return getEnergy(4);
	}

	public void setEnergy4(float energyInKWH) {
		setEnergy(4, energyInKWH);
	}

	public void setEnergy5(float energyInKWH) {
		setEnergy(5, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "6")
	public float getEnergy6() {
		return getEnergy(6);
	}

	public void setEnergy6(float energyInKWH) {
		setEnergy(6, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "7")
	public float getEnergy7() {
		return getEnergy(7);
	}

	public void setEnergy7(float energyInKWH) {
		setEnergy(7, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "8")
	public float getEnergy8() {
		return getEnergy(8);
	}

	public void setEnergy8(float energyInKWH) {
		setEnergy(8, energyInKWH);
	}
}
