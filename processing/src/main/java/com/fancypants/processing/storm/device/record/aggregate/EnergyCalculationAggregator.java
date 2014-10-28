package com.fancypants.processing.storm.device.record.aggregate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.Aggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.RawMeasurementEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.processing.storm.device.record.mapping.EnergyConsumptionTupleMapper;
import com.fancypants.processing.storm.device.record.mapping.RawRecordEntityMapper;
import com.fancypants.usage.generators.DateIntervalGenerator;


public abstract class EnergyCalculationAggregator implements
		Aggregator<RawRecordEntity>, java.util.logging.Filter {

	private static final long serialVersionUID = 1821000328362632362L;
	private static final Logger LOG = LoggerFactory
			.getLogger(EnergyCalculationAggregator.class);

	private static final int SECONDS_PER_HOUR = 60 * 60;
	private static final RawRecordEntityMapper recordMapper = new RawRecordEntityMapper();
	private static final EnergyConsumptionTupleMapper tupleMapper = new EnergyConsumptionTupleMapper();

	private final DateIntervalGenerator generator;

	protected EnergyCalculationAggregator(DateIntervalGenerator generator) {
		this.generator = generator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		LOG.trace("prepare");
	}

	@Override
	public void cleanup() {
		LOG.trace("cleanup");
	}

	@Override
	public boolean isLoggable(LogRecord arg0) {
		LOG.trace("isLoggable", arg0);
		return false;
	}

	@Override
	public RawRecordEntity init(Object batchId, TridentCollector collector) {
		LOG.trace("init", batchId, collector);
		return null;
	}

	@Override
	public void aggregate(RawRecordEntity val, TridentTuple tuple,
			TridentCollector collector) {
		LOG.trace("aggregate entry", val, tuple, collector);
		// de-serialize
		RawRecordEntity recordEntity = recordMapper.convert(tuple);
		// calculator the date that this record will belong in
		Date bucket = generator.flattenDate(recordEntity.getTimestamp());
		// create the energy entity
		EnergyConsumptionRecordEntity energyRecord = new EnergyConsumptionRecordEntity(
				recordEntity.getDevice(), bucket);
		// go through each circuit
		for (Map.Entry<Integer, RawMeasurementEntity> entry : recordEntity
				.getCircuits().entrySet()) {
			// get the measurement
			RawMeasurementEntity measurementEntity = entry.getValue();
			// calculate the energy consumed
			// E = P * t
			// = V * I * t where t is in hours
			float energyInKWH = measurementEntity.getVoltageInVolts()
					* measurementEntity.getAmperageInAmps()
					* recordEntity.getDurationInSeconds() / SECONDS_PER_HOUR;
			// store
			energyRecord.setEnergy(entry.getKey(), energyInKWH);
		}
		// serialize
		List<Object> values = tupleMapper.convert(energyRecord);
		// emit the values
		collector.emit(values);
		LOG.trace("aggregate exit");
	}

	@Override
	public void complete(RawRecordEntity val, TridentCollector collector) {
		LOG.trace("complete");
	}
}
