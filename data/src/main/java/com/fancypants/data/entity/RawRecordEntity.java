package com.fancypants.data.entity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecordEntity {

	public static final String DEVICE_ATTRIBUTE = "device";
	public static final String UUID_ATTRIBUTE = "uuid";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public static final String CIRCUITS_ATTRIBUTE = "circuits";
	public final static String DURATION_ATTRIBUTE = "duration";
	public static final String HASH_KEY = DEVICE_ATTRIBUTE;
	public static final String RANGE_KEY = UUID_ATTRIBUTE;

	@Id
	private final RawRecordId id;
	private final Date timestamp;
	private final float durationInSeconds;
	private final Map<Integer, RawMeasurementEntity> circuits;

	@JsonCreator
	public RawRecordEntity(
			@JsonProperty(DEVICE_ATTRIBUTE) String device,
			@JsonProperty(UUID_ATTRIBUTE) String uuid,
			@JsonProperty(TIMESTAMP_ATTRIBUTE) Date timestamp,
			@JsonProperty(DURATION_ATTRIBUTE) float durationInSeconds,
			@JsonProperty(CIRCUITS_ATTRIBUTE) Map<Integer, RawMeasurementEntity> circuits) {
		this(new RawRecordId(device, UUID.fromString(uuid)), timestamp,
				durationInSeconds, circuits);
	}

	public RawRecordEntity(RawRecordId id, Date timestamp,
			float durationInSeconds, Map<Integer, RawMeasurementEntity> circuits) {
		this.id = id;
		this.timestamp = timestamp;
		this.durationInSeconds = durationInSeconds;
		this.circuits = circuits;
	}

	@JsonIgnore
	public RawRecordId getId() {
		return id;
	}

	@JsonProperty(DEVICE_ATTRIBUTE)
	public String getDevice() {
		return id.getDevice();
	}

	@JsonProperty(UUID_ATTRIBUTE)
	public String getUUID() {
		return id.getUUID().toString();
	}

	@JsonProperty(TIMESTAMP_ATTRIBUTE)
	public Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty(DURATION_ATTRIBUTE)
	public float getDurationInSeconds() {
		return durationInSeconds;
	}

	@JsonProperty(CIRCUITS_ATTRIBUTE)
	public Map<Integer, RawMeasurementEntity> getCircuits() {
		return circuits;
	}

	@JsonIgnore
	public RawMeasurementEntity getCircuit(int index) {
		return circuits.get(index);
	}
}