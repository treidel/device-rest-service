package com.fancypants.storm.device.record.mapping;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import backtype.storm.tuple.ITuple;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.RawMeasurementEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;

@Component
public class RawRecordEntityMapper implements
		EntityMapper<RawRecordEntity, ITuple>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2650439842355334806L;

	@Override
	public RawRecordEntity convert(ITuple tuple) {
		// extract the id attributes
		RawRecordId id = new RawRecordId(
				tuple.getStringByField(RawRecordEntity.DEVICE_ATTRIBUTE),
				UUID.fromString(tuple
						.getStringByField(RawRecordEntity.UUID_ATTRIBUTE)));
		// extract the rest of the fixed attributes
		Date timestamp = new Date(
				tuple.getLongByField(RawRecordEntity.TIMESTAMP_ATTRIBUTE));
		float duration = tuple
				.getFloatByField(RawRecordEntity.DURATION_ATTRIBUTE);
		// extract the measurements
		Map<Integer, RawMeasurementEntity> measurements = new TreeMap<Integer, RawMeasurementEntity>();
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			String voltageField = RawMeasurementEntity.VOLTAGE_ATTRIBUTE + i;
			String amperageField = RawMeasurementEntity.AMPERAGE_ATTRIBUTE + i;
			RawMeasurementEntity measurement = new RawMeasurementEntity(i,
					tuple.getFloatByField(voltageField),
					tuple.getFloatByField(amperageField));
			measurements.put(i, measurement);
		}
		// create the entity
		RawRecordEntity entity = new RawRecordEntity(id, timestamp, duration,
				measurements);
		return entity;
	}

}
