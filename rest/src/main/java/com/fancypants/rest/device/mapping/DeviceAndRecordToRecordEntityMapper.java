package com.fancypants.rest.device.mapping;

import java.text.DateFormat;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.rest.device.domain.Measurement;
import com.fancypants.rest.device.domain.Record;

@Component
public class DeviceAndRecordToRecordEntityMapper implements
		EntityMapper<RecordEntity, Pair<DeviceEntity, Record>> {

	private @Autowired
	DateFormat iso8601DateFormat;
	
	private @Autowired
	CircuitToCircuitEntityMapper mapper;

	@Override
	public RecordEntity convert(Pair<DeviceEntity, Record> entity) {
		// extract the inputs
		DeviceEntity deviceEntity = entity.getLeft();
		Record record = entity.getRight();
		// create + populate the return object
		RecordEntity recordEntity = new RecordEntity();
		recordEntity.setDevice(deviceEntity.getDevice());
		recordEntity.setTimestamp(iso8601DateFormat.format(record
				.getTimestamp()));
		for (Measurement measurement : record.getMeasurements()) {			
			CircuitEntity circuitEntity = deviceEntity.getCircuitByName(measurement.getCircuit());
			recordEntity.setCircuit(circuitEntity.getIndex(), measurement.getValue());
		}
		// done
		return recordEntity;
	}

}
