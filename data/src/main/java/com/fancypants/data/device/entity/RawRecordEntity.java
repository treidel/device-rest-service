package com.fancypants.data.device.entity;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
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

	@JsonProperty(CIRCUITS_ATTRIBUTE)
	public Map<Integer, Float> getCircuits() {
		return circuits;
	}

	@JsonIgnore
	public Float getCircuit(int index) {
		return circuits.get(index);
	}
	
	public static class TEST {
		public static final RawRecordEntity RECORD1; 
		public static final RawRecordEntity RECORD2; 
		public  static final RawRecordId INVALID_RECORD_ID;
		public  static final RawRecordEntity RECORDS[];
		
		static {
			// setup test data
			Map<Integer, Float> energy = new TreeMap<Integer, Float>();
			for (int i = 1; i <= 16; i++) {
				energy.put(i, 10.0f);
			}
			// setup the test records
			RECORD1 = new RawRecordEntity("ABCD1234", UUID.randomUUID().toString(), new Date(), energy);  
			RECORD2 = new RawRecordEntity("ABCD1234", UUID.randomUUID().toString(), new Date(), energy);
			INVALID_RECORD_ID = new RawRecordId("WXYZ7890", UUID.randomUUID());

			// setup the list of records
			RECORDS = new RawRecordEntity[] {RECORD1, RECORD2 };
		}

	}
}
