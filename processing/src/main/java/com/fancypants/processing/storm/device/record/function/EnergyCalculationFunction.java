package com.fancypants.processing.storm.device.record.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawMeasurementEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.processing.storm.device.record.mapping.RawRecordEntityMapper;

public class EnergyCalculationFunction implements Function,
		java.util.logging.Filter {

	private static final long serialVersionUID = 1821000328362632362L;

	private static final int SECONDS_PER_HOUR = 60 * 60;
	private static final RawRecordEntityMapper mapper = new RawRecordEntityMapper();

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLoggable(LogRecord record) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		// decode the record
		RawRecordEntity recordEntity = mapper.convert(tuple);
		// create the value list
		List<Object> values = new ArrayList<Object>(DeviceEntity.MAX_CIRCUITS);
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
			values.add(energyInKWH);
		}
		// emit the values
		collector.emit(values);
	}

}
