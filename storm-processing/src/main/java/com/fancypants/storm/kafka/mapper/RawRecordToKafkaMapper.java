package com.fancypants.storm.kafka.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.storm.device.record.mapping.RawRecordEntityMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backtype.storm.tuple.Tuple;
import storm.kafka.bolt.mapper.TupleToKafkaMapper;

@Component
public class RawRecordToKafkaMapper implements
		TupleToKafkaMapper<String, String> {

	private static final long serialVersionUID = -8711069663212978940L;
	private static final Logger LOG = LoggerFactory
			.getLogger(RawRecordToKafkaMapper.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RawRecordEntityMapper recordMapper;

	@Override
	public String getKeyFromTuple(Tuple tuple) {
		LOG.trace("getKeyFromTuple enter", "tuple", tuple);
		RawRecordEntity record = recordMapper.convert(tuple);
		LOG.trace("getKeyFromTuple exit", record.getDevice());
		return record.getDevice();
	}

	@Override
	public String getMessageFromTuple(Tuple tuple) {
		LOG.trace("getMessageFromTuple enter", "tuple", tuple);
		RawRecordEntity record = recordMapper.convert(tuple);
		try {
			LOG.info("writing record to kafka for device", record.getDevice());
			String value = objectMapper.writeValueAsString(record);
			LOG.trace("getMessageFromTuple exit", value);
			return value;
		} catch (JsonProcessingException e) {
			LOG.error("json error", e);
			throw new IllegalAccessError("json error");
		}
	}

}
