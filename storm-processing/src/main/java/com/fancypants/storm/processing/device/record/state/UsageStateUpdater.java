package com.fancypants.storm.processing.device.record.state;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;

@Component
public class UsageStateUpdater extends BaseStateUpdater<UsageState> {
	private static final Logger LOG = LoggerFactory
			.getLogger(UsageStateUpdater.class);
	private static final long serialVersionUID = 8383124026395269331L;

	@Override
	public void updateState(UsageState state, List<TridentTuple> tuples,
			TridentCollector collector) {
		LOG.trace("UsageStateUpdater.updateState enter", "state", state,
				"tuples", tuples, "collector", collector);
		// iterate through each tuple
		for (TridentTuple tuple : tuples) {
			// get the record
			EnergyConsumptionRecordEntity record = (EnergyConsumptionRecordEntity) tuple
					.get(0);
			// do the write
			state.update(record);
		}
		LOG.trace("UsageStateUpdater.updateState exit");
	}
}
