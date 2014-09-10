package com.fancypants.processing.storm.device.record.scheme;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import backtype.storm.tuple.Fields;

import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.stormspout.IKinesisRecordScheme;
import com.fancypants.stream.device.kinesis.entity.RawRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RawRecordScheme implements IKinesisRecordScheme {

	private static final long serialVersionUID = 2503650846905961717L;

	private final ObjectMapper objectMapper;

	public RawRecordScheme(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<Object> deserialize(Record record) {

		try {
			RawRecord rawRecord = objectMapper.readValue(record.getData()
					.array(), RawRecord.class);
			List<Object> values = new ArrayList<Object>(RawRecord.FIXED_FIELDS.length + rawRecord.getCircuits().size());
			for (String field : RawRecord.FIXED_FIELDS) {
				values.add(BeanUtils.getProperty(rawRecord, field));
			}
			for (Map.Entry<Integer, Float> entry : rawRecord.getCircuits().entrySet()) {
				values.add(entry.getValue().toString());
			}
			return values;
		} catch (IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			// TBD: Log
			return null;
		}
	}

	@Override
	public Fields getOutputFields() {
		List<String> fields = new ArrayList<String>(RawRecord.FIXED_FIELDS.length + RawRecord.MAX_CIRCUITS);
		fields.addAll(Arrays.asList(RawRecord.FIXED_FIELDS));
		for (int i = 1; i <= RawRecord.MAX_CIRCUITS; i++) {
			String field = "circuit" + String.valueOf(i);
			fields.add(field);
		}
		return new Fields(fields);
	}
	
}
