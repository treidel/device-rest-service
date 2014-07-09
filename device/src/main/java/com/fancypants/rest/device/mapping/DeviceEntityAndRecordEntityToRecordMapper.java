package com.fancypants.rest.device.mapping;

import java.sql.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.rest.device.domain.Measurement;
import com.fancypants.rest.device.domain.Record;

@Component
public class DeviceEntityAndRecordEntityToRecordMapper implements
		EntityMapper<Record, Pair<DeviceEntity, RecordEntity>> {

	@Override
	public Record convert(Pair<DeviceEntity, RecordEntity> entity) {
		// extract the entities 
		DeviceEntity deviceEntity = entity.getLeft();
		RecordEntity recordEntity = entity.getRight();
		// create the set for the measurements
		Set<Measurement> measurements = new HashSet<Measurement>(recordEntity
				.getCircuits().size());
		for (Map.Entry<Integer, Float> entry : recordEntity.getCircuits()
				.entrySet()) {
			// pull out the circuit
			CircuitEntity circuit = deviceEntity.getCircuitByIndex(entry.getKey());
			// create the measurement and add it to the set
			Measurement measurement = new Measurement(circuit.getName(),
					entry.getValue());
			measurements.add(measurement);
		}
		// create + return the record
		Record domain = new Record(UUID.fromString(recordEntity.getUUID()),
				Date.valueOf(recordEntity.getTimestamp()), measurements);
		return domain;
	}

}
