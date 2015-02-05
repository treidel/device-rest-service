package com.fancypants.storm.device.record.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backtype.storm.tuple.Fields;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawMeasurementEntity;
import com.fancypants.data.device.entity.RawRecordEntity;

public class RawRecordTupleMapper implements
		EntityMapper<List<Object>, RawRecordEntity>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1640649516827260935L;
	private static final int FIXED_FIELDS_COUNT = 4;

	@Override
	public List<Object> convert(RawRecordEntity entity) {
		List<Object> values = new ArrayList<Object>(FIXED_FIELDS_COUNT
				+ (2 * DeviceEntity.MAX_CIRCUITS));
		// add the fixed fields
		values.add(entity.getDevice());
		values.add(entity.getUUID());
		values.add(entity.getTimestamp().getTime());
		values.add(entity.getDurationInSeconds());
		// add the circuits - this iterates in the order of the index
		for (Map.Entry<Integer, RawMeasurementEntity> entry : entity
				.getCircuits().entrySet()) {
			// get the measurement
			RawMeasurementEntity measurementEntity = entry.getValue();
			// add voltage and amperage
			values.add(measurementEntity.getVoltageInVolts());
			values.add(measurementEntity.getAmperageInAmps());
		}
		// fill in any blank circuits
		for (int i = entity.getCircuits().size() + 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			values.add(0.0f);
			values.add(0.0f);
		}
		return values;
	}

	public static Fields getOutputFields() {
		List<String> fields = new ArrayList<String>(FIXED_FIELDS_COUNT
				+ (2 * DeviceEntity.MAX_CIRCUITS));
		fields.add(RawRecordEntity.DEVICE_ATTRIBUTE);
		fields.add(RawRecordEntity.UUID_ATTRIBUTE);
		fields.add(RawRecordEntity.TIMESTAMP_ATTRIBUTE);
		fields.add(RawRecordEntity.DURATION_ATTRIBUTE);
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			String voltageField = RawMeasurementEntity.VOLTAGE_ATTRIBUTE + i;
			fields.add(voltageField);
			String amperageField = RawMeasurementEntity.AMPERAGE_ATTRIBUTE + i;
			fields.add(amperageField);
		}
		return new Fields(fields);
	}
}
