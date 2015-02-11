package com.fancypants.storm.processing.device.record.state;

import storm.trident.state.State;
import storm.trident.state.ValueUpdater;

import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.repository.HourlyRecordRepository;

public class UsageState implements State,
		ValueUpdater<EnergyConsumptionRecordEntity> {

	private final HourlyRecordRepository repository;

	public UsageState(HourlyRecordRepository repository) {
		this.repository = repository;
	}

	@Override
	public void beginCommit(Long txid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit(Long txid) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnergyConsumptionRecordEntity update(
			EnergyConsumptionRecordEntity stored) {
		// update the counts in the database
		repository.insertOrIncrement(stored);
		// don't emit anything
		return null;
	}

}
