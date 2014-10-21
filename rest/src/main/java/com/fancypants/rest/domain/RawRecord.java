package com.fancypants.rest.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecord {

	public static final String UUID_ATTRIBUTE = "uuid";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public static final String MEASUREMENTS_ATTRIBUTE = "measurements";

	private final UUID uuid;
	private final Date timestamp;
	private final Set<RawMeasurement> measurements;

	@JsonCreator
	public RawRecord(
			@JsonProperty(UUID_ATTRIBUTE) UUID uuid,
			@JsonProperty(TIMESTAMP_ATTRIBUTE) Date timestamp,
			@JsonProperty(MEASUREMENTS_ATTRIBUTE) Set<RawMeasurement> measurements) {
		this.uuid = uuid;
		this.timestamp = timestamp;
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

	@JsonProperty(MEASUREMENTS_ATTRIBUTE)
	public Set<RawMeasurement> getMeasurements() {
		return measurements;
	}

	public static class TEST {
		public static final RawRecord RECORD1;
		public static final RawRecord RECORD2;
		public static final RawRecord RECORDS[];

		static {
			// setup the circuits + test measurements
			Set<CircuitEntity> circuits = new HashSet<CircuitEntity>();
			Set<RawMeasurement> measurements = new HashSet<RawMeasurement>();
			for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
				CircuitEntity circuit = new CircuitEntity(i, "1-" + i, 120.0f,
						10.0f);
				circuits.add(circuit);
				RawMeasurement measurement = new RawMeasurement(
						circuit.getName(), 0.1f);
				measurements.add(measurement);
			}
			// setup the records
			RECORD1 = new RawRecord(UUID.randomUUID(), new Date(), measurements);
			RECORD2 = new RawRecord(UUID.randomUUID(), new Date(), measurements);
			RECORDS = new RawRecord[] { RECORD1, RECORD2 };
		}

	}
}
