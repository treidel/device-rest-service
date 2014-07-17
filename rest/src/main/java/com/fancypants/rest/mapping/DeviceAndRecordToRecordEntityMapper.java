package com.fancypants.rest.mapping;

import java.text.DateFormat;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.rest.domain.CurrentMeasurement;
import com.fancypants.rest.domain.CurrentRecord;

@Component
public class DeviceAndRecordToRecordEntityMapper implements
		EntityMapper<RecordEntity, Pair<DeviceEntity, CurrentRecord>> {

	private @Autowired
	DateFormat iso8601DateFormat;

	private @Autowired
	CircuitToCircuitEntityMapper mapper;

	@Override
	public RecordEntity convert(Pair<DeviceEntity, CurrentRecord> entity) {
		// extract the inputs
		DeviceEntity deviceEntity = entity.getLeft();
		CurrentRecord record = entity.getRight();
		// create + populate the return object
		RecordId recordId = new RecordId();
		recordId.setDevice(deviceEntity.getDevice());
		recordId.setUUID(record.getUUID().toString());
		RecordEntity recordEntity = new RecordEntity();
		recordEntity.setRecordId(recordId);
		recordEntity.setTimestamp(iso8601DateFormat.format(record
				.getTimestamp()));
		for (CurrentMeasurement measurement : record.getMeasurements()) {
			CircuitEntity circuitEntity = deviceEntity
					.getCircuitByName(measurement.getCircuit());
			if (null != circuitEntity) {
				recordEntity.setCircuit(circuitEntity.getIndex(),
						measurement.getCurrent());
			}
		}
		// done
		return recordEntity;
	}

}
