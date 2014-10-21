package com.fancypants.processing.storm.device.record.state;

import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;

import storm.trident.state.State;
import storm.trident.state.ValueUpdater;

public class UsageState implements State,
		ValueUpdater<PowerConsumptionRecordEntity> {

	private final HourlyRecordRepository repository;

	public UsageState(HourlyRecordRepository repository) {
		this.repository = repository;
	}

	@Override
	public void beginCommit(Long txid) {
		// TBD: log
	}

	@Override
	public void commit(Long txid) {
	}

	@Override
	public PowerConsumptionRecordEntity update(
			PowerConsumptionRecordEntity stored) {
		repository.insertOrIncrement(stored);
		return stored;
	}
}
