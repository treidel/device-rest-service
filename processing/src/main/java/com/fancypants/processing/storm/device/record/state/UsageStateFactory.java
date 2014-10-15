package com.fancypants.processing.storm.device.record.state;

import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.fancypants.data.device.repository.HourlyRecordRepository;

public class UsageStateFactory implements StateFactory {

	private static final long serialVersionUID = 296987272885779417L;
	
	private final HourlyRecordRepository repository;

	public UsageStateFactory(HourlyRecordRepository repository) {
		this.repository = repository;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		return new UsageState(repository);
	}

}
