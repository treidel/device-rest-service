package com.fancypants.processing.storm.device.record.scheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.tuple.Fields;

import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.stormspout.IKinesisRecordScheme;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RawRecordScheme implements IKinesisRecordScheme {

	private static final long serialVersionUID = 2503650846905961717L;
	private static final Logger LOG = LoggerFactory
			.getLogger(RawRecordScheme.class);
	private static final int FIXED_FIELDS_COUNT = 3;

	private final ObjectMapper objectMapper;

	public RawRecordScheme(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<Object> deserialize(Record record) {
		LOG.trace("deserialize entry", record);
		try {
			RawRecordEntity entity = objectMapper.readValue(record.getData()
					.array(), RawRecordEntity.class);
			List<Object> values = new ArrayList<Object>(FIXED_FIELDS_COUNT
					+ entity.getCircuits().size());
			// add the fixed fields
			values.add(entity.getDevice());
			values.add(entity.getUUID());
			values.add(entity.getTimestamp().getTime());
			// add the circuits
			for (Map.Entry<Integer, Float> entry : entity.getCircuits()
					.entrySet()) {
				values.add(entry.getValue().toString());
			}
			LOG.trace("deserialize exit values=", values);
			return values;
		} catch (IOException e) {
			LOG.error("unable to deserialize record", record, e);
			return null;
		}
	}

	@Override
	public Fields getOutputFields() {
		List<String> fields = new ArrayList<String>(FIXED_FIELDS_COUNT
				+ DeviceEntity.MAX_CIRCUITS);
		fields.add(RawRecordEntity.DEVICE_ATTRIBUTE);
		fields.add(RawRecordEntity.UUID_ATTRIBUTE);
		fields.add(RawRecordEntity.TIMESTAMP_ATTRIBUTE);
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			String field = "circuit" + String.valueOf(i);
			fields.add(field);
		}
		return new Fields(fields);
	}

}
