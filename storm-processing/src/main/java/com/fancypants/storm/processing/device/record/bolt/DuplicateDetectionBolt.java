package com.fancypants.storm.processing.device.record.bolt;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.repository.RawRecordRepository;
import com.fancypants.storm.device.record.mapping.RawRecordEntityMapper;
import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;

@Component
public class DuplicateDetectionBolt extends BaseRichBolt {

	private static final Logger LOG = LoggerFactory
			.getLogger(DuplicateDetectionBolt.class);

	private static final long serialVersionUID = 2209361591380557519L;

	@Autowired
	private RawRecordRepository recordRepository;

	@Autowired
	private RawRecordEntityMapper recordMapper;

	@Autowired
	private RawRecordTupleMapper tupleMapper;

	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		LOG.trace("DuplicateDetectionBolt.prepare enter", "stormConf",
				stormConf, "context", context, "collector", collector);
		this.collector = collector;
		LOG.trace("DuplicateDetectionBolt.prepare exit");
	}

	@Override
	public void execute(Tuple input) {
		LOG.trace("DuplicateDetectionBolt.execute enter", "input", input);
		// get the record
		RawRecordEntity record = recordMapper.convert(input);
		// try to insert
		if (true == recordRepository.insert(record)) {
			// not a duplicate so we pass it on
			List<Object> values = tupleMapper.convert(record);
			collector.emit(values);
		}

		LOG.trace("DuplicateDetectionBolt.execute exit");
	}

	@Override
	public void cleanup() {
		LOG.trace("DuplicateDetectorBolt.cleanup enter");
		LOG.trace("DuplicateDetectorBolt.cleanup exit");
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		LOG.trace("DuplicateDetectorBolt.declareOutputFields enter",
				"declarer", declarer);
		declarer.declare(RawRecordTupleMapper.getOutputFields());
		LOG.trace("DuplicateDetectorBolt.declareOutputFields exit");
	}

}
