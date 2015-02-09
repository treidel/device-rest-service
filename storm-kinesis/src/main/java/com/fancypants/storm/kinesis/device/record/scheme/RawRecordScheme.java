package com.fancypants.storm.kinesis.device.record.scheme;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.tuple.Fields;

import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.stormspout.IKinesisRecordScheme;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RawRecordScheme implements IKinesisRecordScheme {

	private static final long serialVersionUID = 2503650846905961717L;
	private static final Logger LOG = LoggerFactory
			.getLogger(RawRecordScheme.class);

	private final ObjectMapper objectMapper;
	private final RawRecordTupleMapper mapper = new RawRecordTupleMapper();

	public RawRecordScheme(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public List<Object> deserialize(Record record) {
		LOG.trace("deserialize entry", record);
		try {
			RawRecordEntity entity = objectMapper.readValue(record.getData()
					.array(), RawRecordEntity.class);
			// map to attributes
			List<Object> values = mapper.convert(entity);
			LOG.trace("deserialize exit values=", values);
			return values;
		} catch (IOException e) {
			LOG.error("unable to deserialize record", record, e);
			return null;
		}
	}

	@Override
	public Fields getOutputFields() {
		return RawRecordTupleMapper.getOutputFields();
	}

}
