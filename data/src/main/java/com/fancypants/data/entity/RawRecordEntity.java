package com.fancypants.data.entity;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecordEntity {

	public final static String RECORDID_ATTRIBUTE = "recordId";
	public final static String DEVICE_ATTRIBUTE = "device";
	public final static String UUID_ATTRIBUTE = "uuid";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public static final String CIRCUITS_ATTRIBUTE = "circuits";
	public final static String DURATION_ATTRIBUTE = "duration";
	public static final String HASH_KEY = RECORDID_ATTRIBUTE;

	@Id
	private final RawRecordId id;
	private final Date timestamp;
	private final float durationInSeconds;
	private final Map<Integer, RawMeasurementEntity> circuits;

	@JsonCreator
	public RawRecordEntity(
			@JsonProperty(RECORDID_ATTRIBUTE) RawRecordId id,
			@JsonProperty(DEVICE_ATTRIBUTE) String device,
			@JsonProperty(UUID_ATTRIBUTE) String uuid,
			@JsonProperty(TIMESTAMP_ATTRIBUTE) Date timestamp,
			@JsonProperty(DURATION_ATTRIBUTE) float durationInSeconds,
			@JsonProperty(CIRCUITS_ATTRIBUTE) Map<Integer, RawMeasurementEntity> circuits) {
		this.id = id;
		this.timestamp = timestamp;
		this.durationInSeconds = durationInSeconds;
		this.circuits = circuits;
	}

	public RawRecordEntity(RawRecordId id, Date timestamp,
			float durationInSeconds, Map<Integer, RawMeasurementEntity> circuits) {
		this(id, id.getDevice(), id.getUUID().toString(), timestamp,
				durationInSeconds, circuits);
	}

	@JsonProperty(RECORDID_ATTRIBUTE)
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