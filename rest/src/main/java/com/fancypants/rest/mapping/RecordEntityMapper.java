package com.fancypants.rest.mapping;

import java.text.DateFormat;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordId;
import com.fancypants.rest.domain.CurrentMeasurement;
import com.fancypants.rest.domain.CurrentRecord;

@Component
public class RecordEntityMapper implements
		EntityMapper<RawRecordEntity, Pair<DeviceEntity, CurrentRecord>> {

	private @Autowired
	DateFormat iso8601DateFormat;

	private @Autowired
	CircuitEntityMapper mapper;

	@Override
	public RawRecordEntity convert(Pair<DeviceEntity, CurrentRecord> entity) {
		// extract the inputs
		DeviceEntity deviceEntity = entity.getLeft();
		CurrentRecord record = entity.getRight();
		// create + populate the return object
		RawRecordId recordId = new RawRecordId();
		recordId.setDevice(deviceEntity.getDevice());
		recordId.setUUID(record.getUUID().toString());
		RawRecordEntity recordEntity = new RawRecordEntity();
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
