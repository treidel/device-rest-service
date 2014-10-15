package com.fancypants.processing.storm.device.record.state;

import storm.trident.state.State;

import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.repository.HourlyRecordRepository;

public class UsageState implements State {

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
	
	public void save(PowerConsumptionRecordEntity entity) {
		// store the entity
		repository.insertOrIncrement(entity);
	}
}
