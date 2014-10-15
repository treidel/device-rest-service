package com.fancypants.data.device.entity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecordEntity {

	public static final String TABLE_NAME = "raw";
	public static final String DEVICE_ATTRIBUTE = "device";
	public static final String UUID_ATTRIBUTE = "uuid";
	public static final String TIMESTAMP_ATTRIBUTE = "timestamp";
	public static final String CIRCUITS_ATTRIBUTE = "circuits";
	public static final String HASH_KEY = DEVICE_ATTRIBUTE;
	public static final String RANGE_KEY = UUID_ATTRIBUTE;
	public static final String[] FIXED_FIELDS = {DEVICE_ATTRIBUTE, UUID_ATTRIBUTE, TIMESTAMP_ATTRIBUTE};
	
	private final RawRecordId id;
	private final Date timestamp;
	private final Map<Integer, Float> circuits;

	@JsonCreator
	public RawRecordEntity(@JsonProperty(DEVICE_ATTRIBUTE) String device,
			@JsonProperty(UUID_ATTRIBUTE) String uuid,
			@JsonProperty(TIMESTAMP_ATTRIBUTE) Date timestamp,
			@JsonProperty(CIRCUITS_ATTRIBUTE) Map<Integer, Float> circuits) {
		this(new RawRecordId(device, UUID.fromString(uuid)), timestamp,
				circuits);
	}

	public RawRecordEntity(RawRecordId id, Date timestamp,
			Map<Integer, Float> circuits) {
		this.id = id;
		this.timestamp = timestamp;
		this.circuits = circuits;
	}

	@Id
	@JsonIgnore
	public RawRecordId getId() {
		return id;
	}

	public String getDevice() {
		return id.getDevice();
	}

	public String getUUID() {
		return id.getUUID().toString();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Map<Integer, Float> getCircuits() {
		return circuits;
	}

	@JsonIgnore
	public Float getCircuit(int index) {
		return circuits.get(index);
	}
}
