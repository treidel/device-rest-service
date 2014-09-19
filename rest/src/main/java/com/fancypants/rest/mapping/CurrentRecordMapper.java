package com.fancypants.rest.mapping;

import java.sql.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.rest.domain.CurrentMeasurement;
import com.fancypants.rest.domain.CurrentRecord;

@Component
public class CurrentRecordMapper implements
		EntityMapper<CurrentRecord, Pair<DeviceEntity, RawRecordEntity>> {

	@Override
	public CurrentRecord convert(Pair<DeviceEntity, RawRecordEntity> entity) {
		// extract the entities 
		DeviceEntity deviceEntity = entity.getLeft();
		RawRecordEntity recordEntity = entity.getRight();
		// create the set for the measurements
		Set<CurrentMeasurement> measurements = new HashSet<CurrentMeasurement>(recordEntity
				.getCircuits().size());
		for (Map.Entry<Integer, Float> entry : recordEntity.getCircuits()
				.entrySet()) {
			// pull out the circuit
			CircuitEntity circuit = deviceEntity.getCircuitByIndex(entry.getKey());
			// create the measurement and add it to the set
			CurrentMeasurement measurement = new CurrentMeasurement(circuit.getName(),
					entry.getValue());
			measurements.add(measurement);
		}
		// create + return the record
		CurrentRecord domain = new CurrentRecord(UUID.fromString(recordEntity.getUUID()),
				Date.valueOf(recordEntity.getTimestamp()), measurements);
		return domain;
	}

}
