package com.fancypants.processing.storm.device.record.function;

import java.util.Date;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

import com.fancypants.usage.generators.DateIntervalGenerator;

public class TimeGroupingFunction extends BaseFunction {

	private static final long serialVersionUID = -8290600345138859823L;

	private final DateIntervalGenerator generator;
	
	public TimeGroupingFunction(DateIntervalGenerator generator) {
		this.generator = generator;
	}
	
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		// extract the timestamp
		Date timestamp = new Date(tuple.getLong(0));
		// squish the timestamp 
		Date bucket = generator.flattenDate(timestamp);
		// done
		collector.emit(new Values(bucket.getTime()));
	}

}
