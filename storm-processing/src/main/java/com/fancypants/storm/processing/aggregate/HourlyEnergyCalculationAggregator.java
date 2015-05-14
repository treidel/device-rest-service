package com.fancypants.storm.processing.aggregate;

import org.springframework.stereotype.Component;

import com.fancypants.usage.generators.HourlyDateIntervalGenerator;

@Component
public class HourlyEnergyCalculationAggregator extends EnergyCalculationAggregator {

	private static final long serialVersionUID = -3577339296364432306L;

	public HourlyEnergyCalculationAggregator() {
		super(new HourlyDateIntervalGenerator());
	}
}
