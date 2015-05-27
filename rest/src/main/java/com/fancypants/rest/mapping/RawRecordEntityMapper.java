package com.fancypants.rest.mapping;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.common.exception.DataValidationException;
import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.RawMeasurementEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.rest.domain.RawMeasurement;
import com.fancypants.rest.domain.RawRecord;

@Component
public class RawRecordEntityMapper implements
		EntityMapper<RawRecordEntity, Pair<DeviceEntity, RawRecord>> {

	private @Autowired
	CircuitEntityMapper mapper;

	@Override
	public RawRecordEntity convert(Pair<DeviceEntity, RawRecord> entity)
			throws DataValidationException {
		// extract the inputs
		DeviceEntity deviceEntity = entity.getLeft();
		RawRecord record = entity.getRight();
		// make sure we have data for each circuit
		if (deviceEntity.getCircuits().size() != record.getMeasurements()
				.size()) {
			throw new DataValidationException("mismatched circuit count "
					+ deviceEntity.getCircuits().size() + "!="
					+ record.getMeasurements().size());
		}
		// create the list of circuits
		Map<Integer, RawMeasurementEntity> circuits = new TreeMap<Integer, RawMeasurementEntity>();
		for (RawMeasurement measurement : record.getMeasurements()) {
			CircuitEntity circuitEntity = deviceEntity
					.getCircuitByName(measurement.getCircuit());
			if (null != circuitEntity) {
				RawMeasurementEntity measurementEntity = new RawMeasurementEntity(
						circuitEntity.getIndex(),
						measurement.getVoltageInVolts(),
						measurement.getAmperageInAmps());
				circuits.put(circuitEntity.getIndex(), measurementEntity);
			} else {
				throw new DataValidationException("invalid circuit="
						+ measurement.getCircuit());
			}
		}
		// create the entity
		RawRecordEntity recordEntity = new RawRecordEntity(new RawRecordId(
				deviceEntity.getDevice(), record.getUUID()),
				record.getTimestamp(), record.getDurationInSeconds(), circuits);
		// done
		return recordEntity;
	}

}
