package com.fancypants.rest.device.mapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.Circuit;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.domain.Measurement;
import com.fancypants.rest.device.domain.Record;

@Component
public class DeviceAndRecordToRecordEntityMapper implements
		EntityMapper<RecordEntity, Pair<Device, Record>> {

	private @Autowired
	DateFormat iso8601DateFormat;

	static {
		// use ISO8601/RFC3339 time format
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
	}

	@Override
	public RecordEntity convert(Pair<Device, Record> entity) {
		// extract the inputs
		Device device = entity.getLeft();
		Record record = entity.getRight();
		// create + populate the return object
		RecordEntity recordEntity = new RecordEntity();
		recordEntity.setDevice(device.getName());
		recordEntity.setTimestamp(iso8601DateFormat.format(record
				.getTimestamp()));
		for (Measurement measurement : record.getMeasurements()) {
			Circuit circuit = device.getCircuitByName(measurement.getCircuit());
			recordEntity.setCircuit(circuit.getIndex(), measurement.getValue());
		}
		// done
		return recordEntity;
	}

}
