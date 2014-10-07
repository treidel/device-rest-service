package com.fancypants.stream.kinesis.entity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawRecord implements KinesisRecord {

	public final static String[] FIXED_FIELDS = { "device", "uuid", "timestamp",
			"circuits", "duration"};
	public final static int MAX_CIRCUITS = 32;

	private final String device;
	private final UUID uuid;
	private final Date timestamp;
	private final Map<Integer, Float> circuits;
	private final float duration;

	@JsonCreator
	public RawRecord(@JsonProperty("device") String device,
			@JsonProperty("uuid") UUID uuid,
			@JsonProperty("timestamp") Date timestamp,
			@JsonProperty("circuits") Map<Integer, Float> circuits,
			@JsonProperty("duration") float duration) {
		this.device = device;
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.circuits = circuits;
		this.duration = duration;
	}

	@Override
	public String getPartitionKey() {
		return device;
	}

	public String getDevice() {
		return device;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public float getDuration() {
		return duration;
	}

	public Map<Integer, Float> getCircuits() {
		return circuits;
	}
}
