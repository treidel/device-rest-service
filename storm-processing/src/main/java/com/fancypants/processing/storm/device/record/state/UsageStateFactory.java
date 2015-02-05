package com.fancypants.processing.storm.device.record.state;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

import com.fancypants.data.device.repository.HourlyRecordRepository;

@Component
public class UsageStateFactory implements StateFactory {

	private static final long serialVersionUID = 296987272885779417L;

	@Autowired
	private HourlyRecordRepository repository;

	@SuppressWarnings("rawtypes")
	@Override
	public State makeState(Map conf, IMetricsContext metrics,
			int partitionIndex, int numPartitions) {
		// create the backing map
		UsageState state = new UsageState(repository);
		return state;
	}

}
