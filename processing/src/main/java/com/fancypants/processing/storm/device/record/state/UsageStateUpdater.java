package com.fancypants.processing.storm.device.record.state;

import java.util.List;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.processing.storm.device.record.mapping.PowerConsumptionRecordEntityMapper;

public class UsageStateUpdater extends BaseStateUpdater<UsageState> {

	private static final long serialVersionUID = 8383124026395269331L;

	private final PowerConsumptionRecordEntityMapper mapper = new PowerConsumptionRecordEntityMapper();

	@Override
	public void updateState(UsageState state, List<TridentTuple> tuples,
			TridentCollector collector) {
		// iterate through each tuple
		for (TridentTuple tuple : tuples) {
			// deserialize the entity
			PowerConsumptionRecordEntity entity = mapper.convert(tuple);
			// do the write
			state.update(entity);
		}
	}
}
