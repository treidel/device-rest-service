package com.fancypants.storm.kafka.scheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backtype.storm.spout.MultiScheme;
import backtype.storm.tuple.Fields;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RawRecordScheme implements MultiScheme {

	private static final long serialVersionUID = 2503650846905961717L;
	private static final Logger LOG = LoggerFactory
			.getLogger(RawRecordScheme.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RawRecordTupleMapper mapper;

	@Override
	public Iterable<List<Object>> deserialize(byte[] ser) {

		LOG.trace("deserialize entry", ser);
		// parse the JSON
		try {
			RawRecordEntity entity = objectMapper.readValue(ser,
					RawRecordEntity.class);
			// map to attributes
			List<Object> values = mapper.convert(entity);
			LOG.trace("deserialize exit values=", values);
			List<List<Object>> valuesList = new ArrayList<List<Object>>(1);
			valuesList.add(values);
			return valuesList;
		} catch (IOException e) {
			LOG.error("unable to deserialize data", ser, e);
			return null;
		}
	}

	@Override
	public Fields getOutputFields() {
		return RawRecordTupleMapper.getOutputFields();
	}

}
