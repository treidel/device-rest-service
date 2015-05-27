package com.fancypants.rest.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fancypants.data.entity.DeviceEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecord {

	public static final String UUID_ATTRIBUTE = "uuid";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public final static String DURATION_ATTRIBUTE = "duration-in-s";
	public static final String MEASUREMENTS_ATTRIBUTE = "measurements";

	private final UUID uuid;
	private final Date timestamp;
	private final float durationInSeconds;
	private final Set<RawMeasurement> measurements;

	@JsonCreator
	public RawRecord(
			@JsonProperty(value = UUID_ATTRIBUTE, required = true) UUID uuid,
			@JsonProperty(value = TIMESTAMP_ATTRIBUTE, required = true) Date timestamp,
			@JsonProperty(value = DURATION_ATTRIBUTE, required = true) float durationInSeconds,
			@JsonProperty(value = MEASUREMENTS_ATTRIBUTE, required = true) Set<RawMeasurement> measurements) {
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.durationInSeconds = durationInSeconds;
		this.measurements = measurements;
	}

	@JsonProperty(UUID_ATTRIBUTE)
	public UUID getUUID() {
		return uuid;
	}

	@JsonProperty(TIMESTAMP_ATTRIBUTE)
	public Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty(DURATION_ATTRIBUTE)
	public Float getDurationInSeconds() {
		return durationInSeconds;
	}

	@JsonProperty(MEASUREMENTS_ATTRIBUTE)
	public Set<RawMeasurement> getMeasurements() {
		return measurements;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("RawRecord[");
		buffer.append("uuid=");
		buffer.append(uuid.toString());
		buffer.append(',');
		buffer.append("timestamp=");
		buffer.append(timestamp.toString());
		buffer.append(',');
		buffer.append("durationInSeconds=");
		buffer.append(String.valueOf(durationInSeconds));
		buffer.append(',');
		buffer.append("measurements=[");
		for (RawMeasurement measurement : measurements) {
			buffer.append(measurement.toString());
		}
		buffer.append(']');
		buffer.append(']');
		return buffer.toString();
	}

	public static class TEST {
		public static final RawRecord RECORD1;
		public static final RawRecord RECORD2;
		public static final RawRecord RECORDS[];

		static {
			// setup the test measurements
			Set<RawMeasurement> measurements = new HashSet<RawMeasurement>();
			for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
				RawMeasurement measurement = new RawMeasurement("1-" + i,
						120.0f, 10.0f);
				measurements.add(measurement);
			}
			// setup the records
			RECORD1 = new RawRecord(UUID.randomUUID(), new Date(), 10.0f,
					measurements);
			RECORD2 = new RawRecord(UUID.randomUUID(), new Date(), 10.0f,
					measurements);
			RECORDS = new RawRecord[] { RECORD1, RECORD2 };
		}

	}
}
