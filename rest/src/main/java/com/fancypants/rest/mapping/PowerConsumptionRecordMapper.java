package com.fancypants.rest.mapping;

import java.sql.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;
import com.fancypants.rest.domain.PowerConsumptionMeasurement;
import com.fancypants.rest.domain.PowerConsumptionRecord;

@Component
public class PowerConsumptionRecordMapper
		implements
		EntityMapper<PowerConsumptionRecord, Pair<DeviceEntity, PowerConsumptionRecordEntity>> {

	@Override
	public PowerConsumptionRecord convert(
			Pair<DeviceEntity, PowerConsumptionRecordEntity> value) {

		Set<PowerConsumptionMeasurement> measurements = new HashSet<PowerConsumptionMeasurement>(
				value.getValue().getEnergy().size());
		for (Map.Entry<Integer, Float> entry : value.getValue().getEnergy()
				.entrySet()) {
			CircuitEntity circuit = value.getKey().getCircuitByIndex(
					entry.getKey());
			PowerConsumptionMeasurement measurement = new PowerConsumptionMeasurement(
					circuit.getName(), entry.getValue());
			measurements.add(measurement);
		}
		// create the record
		PowerConsumptionRecord record = new PowerConsumptionRecord(
				Date.valueOf(value.getValue().getDate()), measurements);
		// done
		return record;
	}
}
