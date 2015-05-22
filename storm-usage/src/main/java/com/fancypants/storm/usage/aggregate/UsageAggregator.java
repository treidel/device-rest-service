package com.fancypants.storm.usage.aggregate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import storm.trident.operation.ReducerAggregator;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.storm.device.record.mapping.EnergyConsumptionEntityMapper;
import com.fancypants.usage.summarizer.EnergyConsumptionSummarizer;

@Component
public class UsageAggregator implements
		ReducerAggregator<EnergyConsumptionRecordEntity> {

	private static final Logger LOG = LoggerFactory
			.getLogger(UsageAggregator.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -313154810699858938L;

	@Autowired
	private EnergyConsumptionEntityMapper mapper;

	@Autowired
	private EnergyConsumptionSummarizer summarizer;

	@Override
	public EnergyConsumptionRecordEntity init() {
		LOG.trace("EnergyConsumptionRecordEntity.init enter");
		// indicate null for a new period of time
		LOG.trace("EnergyConsumptionRecordEntity.init exit");
		return null;
	}

	@Override
	public EnergyConsumptionRecordEntity reduce(
			EnergyConsumptionRecordEntity curr, TridentTuple tuple) {
		LOG.trace("EnergyConsumptionRecordEntity.reduce enter", "curr", curr,
				"tuple", tuple);
		// decode the tuple
		EnergyConsumptionRecordEntity value = mapper.convert(tuple);

		LOG.info("summarizing usage", "device", value.getDevice());

		// only for subsequent invocations do we summarize
		if (null != curr) {
			// sum the two records together
			value = summarizer.summarize(curr.getId(), curr, value);
		}
		LOG.trace("EnergyConsumptionRecordEntity.reduce exit", value);
		return value;
	}

}
