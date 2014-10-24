package com.fancypants.rest.mapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawMeasurementEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.rest.domain.RawMeasurement;
import com.fancypants.rest.domain.RawRecord;

@Component
public class RawRecordMapper implements
		EntityMapper<RawRecord, Pair<DeviceEntity, RawRecordEntity>> {

	@Override
	public RawRecord convert(Pair<DeviceEntity, RawRecordEntity> entity) {
		// extract the entities
		DeviceEntity deviceEntity = entity.getLeft();
		RawRecordEntity recordEntity = entity.getRight();
		// create the set for the measurements
		Set<RawMeasurement> measurements = new HashSet<RawMeasurement>(
				recordEntity.getCircuits().size());
		for (Map.Entry<Integer, RawMeasurementEntity> entry : recordEntity
				.getCircuits().entrySet()) {
			// pull out the circuit
			CircuitEntity circuit = deviceEntity.getCircuitByIndex(entry
					.getKey());
			RawMeasurementEntity measurementEntity = entry.getValue();
			// create the measurement and add it to the set
			RawMeasurement measurement = new RawMeasurement(circuit.getName(),
					measurementEntity.getVoltageInVolts(),
					measurementEntity.getAmperageInAmps(),
					measurementEntity.getDurationInSeconds());
			measurements.add(measurement);
		}
		// create + return the record
		RawRecord domain = new RawRecord(
				UUID.fromString(recordEntity.getUUID()),
				recordEntity.getTimestamp(), measurements);
		return domain;
	}
}
