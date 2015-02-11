package com.fancypants.rest.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
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
			@JsonProperty(DATE_ATTRIBUTE) Date date,
			@JsonProperty(MEASUREMENTS_ATTRIBUTE) Set<PowerConsumptionMeasurement> measurements) {
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
			Set<PowerConsumptionMeasurement> measurements = new HashSet<PowerConsumptionMeasurement>();
			for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
				PowerConsumptionMeasurement measurement = new PowerConsumptionMeasurement(
						i + "-1", 0.1f);
				measurements.add(measurement);
			}
			Calendar calendar = Calendar.getInstance(TimeZone
					.getTimeZone("GMT"));
			calendar.setTime(new Date());
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date date1 = calendar.getTime();
			calendar.add(Calendar.HOUR, 1);
			Date date2 = calendar.getTime();
			RECORD1 = new PowerConsumptionRecord(date1, measurements);
			RECORD2 = new PowerConsumptionRecord(date2, measurements);
			RECORDS = new PowerConsumptionRecord[] { RECORD1, RECORD2 };
		}

	}

}
