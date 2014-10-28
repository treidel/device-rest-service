package com.fancypants.processing.storm.device.record.aggregate;

import org.springframework.stereotype.Component;

import storm.trident.operation.ReducerAggregator;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.processing.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.usage.summarizer.EnergyConsumptionSummarizer;

@Component
public class UsageAggregator implements
		ReducerAggregator<EnergyConsumptionRecordEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -313154810699858938L;

	private static final EnergyConsumptionEntityMapper mapper = new EnergyConsumptionEntityMapper();
	private static final EnergyConsumptionSummarizer summarizer = new EnergyConsumptionSummarizer();

	@Override
	public EnergyConsumptionRecordEntity init() {
		// indicate null for a new period of time
		return null;
	}

	@Override
	public EnergyConsumptionRecordEntity reduce(
			EnergyConsumptionRecordEntity curr, TridentTuple tuple) {
		// decode the tuple
		EnergyConsumptionRecordEntity newEntity = mapper.convert(tuple);
		// see if this is the first record for the time period
		if (null == curr) {
			// we're done!
			return newEntity;
		} else {
			// sum the two records together
			return summarizer.summarize(curr.getId(), curr, newEntity);
		}
	}

}
