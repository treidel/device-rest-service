package com.fancypants.rest.mapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.rest.domain.PowerConsumptionMeasurement;
import com.fancypants.rest.domain.PowerConsumptionRecord;

@Component
public class PowerConsumptionRecordMapper
		implements
		EntityMapper<PowerConsumptionRecord, Pair<DeviceEntity, EnergyConsumptionRecordEntity>> {

	@Override
	public PowerConsumptionRecord convert(
			Pair<DeviceEntity, EnergyConsumptionRecordEntity> value) {

		Set<PowerConsumptionMeasurement> measurements = new HashSet<PowerConsumptionMeasurement>(
				value.getValue().getEnergy().size());
		for (Map.Entry<Integer, Float> entry : value.getValue().getEnergy()
				.entrySet()) {
			// get the index
			int index = entry.getKey();
			// resolve the circuit
			CircuitEntity circuit = value.getKey().getCircuitByIndex(index);
			// if the circuit does not exist it's because the records provide
			// data for index=max
			if (null != circuit) {
				PowerConsumptionMeasurement measurement = new PowerConsumptionMeasurement(
						circuit.getName(), entry.getValue());
				measurements.add(measurement);
			}
		}
		// create the record
		PowerConsumptionRecord record = new PowerConsumptionRecord(value
				.getValue().getDate(), measurements);
		// done
		return record;
	}
}
