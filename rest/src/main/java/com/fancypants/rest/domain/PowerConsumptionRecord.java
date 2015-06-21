package com.fancypants.rest.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import com.fancypants.data.entity.DeviceEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerConsumptionRecord implements
		Comparable<PowerConsumptionRecord> {

	public static final String DATE_ATTRIBUTE = "date";
	public static final String MEASUREMENTS_ATTRIBUTE = "measurements";

	private final Date date;
	private final Set<PowerConsumptionMeasurement> measurements;

	@JsonCreator
	public PowerConsumptionRecord(
			@JsonProperty(value = DATE_ATTRIBUTE, required = true) Date date,
			@JsonProperty(value = MEASUREMENTS_ATTRIBUTE, required = true) Set<PowerConsumptionMeasurement> measurements) {
		this.date = date;
		this.measurements = measurements;
	}

	@Override
	public int compareTo(PowerConsumptionRecord record) {
		return this.date.compareTo(record.date);
	}

	@JsonProperty(DATE_ATTRIBUTE)
	public Date getDate() {
		return date;
	}

	@JsonProperty(MEASUREMENTS_ATTRIBUTE)
	public Set<PowerConsumptionMeasurement> getMeasurements() {
		return measurements;
	}

	public static class TEST {
		public static final PowerConsumptionRecord RECORD1;
		public static final PowerConsumptionRecord RECORD2;
		public static final PowerConsumptionRecord RECORDS[];

		static {
			Random rand = new Random();
			Calendar calendar = Calendar.getInstance(TimeZone
					.getTimeZone("GMT"));
			calendar.setTime(new Date());
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			List<PowerConsumptionRecord> records = new ArrayList<>(24);
			for (int i = 0; i < 24; i++) {
				Set<PowerConsumptionMeasurement> measurements = new HashSet<PowerConsumptionMeasurement>();
				for (int j = 1; j <= DeviceEntity.MAX_CIRCUITS; j++) {
					PowerConsumptionMeasurement measurement = new PowerConsumptionMeasurement(
							i + "-1", rand.nextFloat() * 100);
					measurements.add(measurement);
				}
				Date timestamp = calendar.getTime();
				PowerConsumptionRecord record = new PowerConsumptionRecord(
						timestamp, measurements);
				records.add(record);
				calendar.add(Calendar.HOUR, 1);
			}
			RECORDS = new PowerConsumptionRecord[24];
			records.toArray(RECORDS);
			RECORD1 = RECORDS[0];
			RECORD2 = RECORDS[1];
		}

	}

}
