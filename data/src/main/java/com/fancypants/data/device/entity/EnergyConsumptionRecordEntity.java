package com.fancypants.data.device.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnergyConsumptionRecordEntity {
	public static final String TABLE_NAME = "hourly";
	public static final String DEVICE_ATTRIBUTE = "device";
	public static final String DATE_ATTRIBUTE = "date";
	public static final String ENERGY_IN_KWH_ATTRIBUTE_PREFIX = "energy-in-kwh-";
	public static final String HASH_KEY = DEVICE_ATTRIBUTE;
	public static final String RANGE_KEY = DATE_ATTRIBUTE;

	private final EnergyConsumptionRecordId id;
	private final Map<Integer, Float> energyMap;

	@JsonCreator
	public EnergyConsumptionRecordEntity(
			@JsonProperty(DEVICE_ATTRIBUTE) String device,
			@JsonProperty(DATE_ATTRIBUTE) Date date) {
		this.id = new EnergyConsumptionRecordId(device, date);
		this.energyMap = new TreeMap<Integer, Float>();
	}

	public EnergyConsumptionRecordEntity(EnergyConsumptionRecordId id,
			Map<Integer, Float> energy) {
		this.id = id;
		this.energyMap = energy;
	}

	public EnergyConsumptionRecordEntity(EnergyConsumptionRecordId id) {
		this.id = id;
		this.energyMap = new TreeMap<Integer, Float>();
	}

	@Id
	@JsonIgnore
	public EnergyConsumptionRecordId getId() {
		return id;
	}

	@JsonProperty(DEVICE_ATTRIBUTE)
	public String getDevice() {
		return id.getDevice();
	}

	@JsonProperty(DATE_ATTRIBUTE)
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

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "5")
	public float getEnergy5() {
		return getEnergy(5);
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

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "9")
	public float getEnergy9() {
		return getEnergy(9);
	}

	public void setEnergy9(float energyInKWH) {
		setEnergy(9, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "10")
	public float getEnergy10() {
		return getEnergy(10);
	}

	public void setEnergy10(float energyInKWH) {
		setEnergy(10, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "11")
	public float getEnergy11() {
		return getEnergy(11);
	}

	public void setEnergy11(float energyInKWH) {
		setEnergy(11, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "12")
	public float getEnergy12() {
		return getEnergy(12);
	}

	public void setEnergy12(float energyInKWH) {
		setEnergy(12, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "13")
	public float getEnergy13() {
		return getEnergy(13);
	}

	public void setEnergy13(float energyInKWH) {
		setEnergy(13, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "14")
	public float getEnergy14() {
		return getEnergy(14);
	}

	public void setEnergy14(float energyInKWH) {
		setEnergy(14, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "15")
	public float getEnergy15() {
		return getEnergy(15);
	}

	public void setEnergy15(float energyInKWH) {
		setEnergy(15, energyInKWH);
	}

	@JsonProperty(ENERGY_IN_KWH_ATTRIBUTE_PREFIX + "16")
	public float getEnergy16() {
		return getEnergy(16);
	}

	public void setEnergy16(float energyInKWH) {
		setEnergy(16, energyInKWH);
	}

	public static class TEST {
		public static final EnergyConsumptionRecordEntity RECORD1;
		public static final EnergyConsumptionRecordEntity RECORD2;
		public static final EnergyConsumptionRecordEntity RECORDS[];

		static {
			// get the current time
			Date currentTime = new Date();
			// use GMT time
			Calendar calendar = Calendar.getInstance(TimeZone
					.getTimeZone("GMT"));
			// populate the current time
			calendar.setTime(currentTime);
			// want times on hour boundary
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			// find the start time of the hour
			Date startOfHour = calendar.getTime();
			// find the nextday of the next
			calendar.add(Calendar.HOUR, 1);
			Date endOfHour = calendar.getTime();

			// setup the test records
			RECORD1 = new EnergyConsumptionRecordEntity("ABCD1234", startOfHour);
			for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
				RECORD1.setEnergy(i, 10.0f);
			}
			RECORD2 = new EnergyConsumptionRecordEntity("ABCD1234", endOfHour);
			for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
				RECORD2.setEnergy(i, 20.0f);
			}

			// setup the list of records
			RECORDS = new EnergyConsumptionRecordEntity[] { RECORD1, RECORD2 };
		}
	}
}
