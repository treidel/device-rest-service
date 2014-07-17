package com.fancypants.rest.usage;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.rest.domain.CurrentMeasurement;
import com.fancypants.rest.domain.CurrentRecord;
import com.fancypants.rest.domain.PowerConsumptionMeasurement;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fancypants.rest.request.DeviceContainer;

@Component
public class UsageSummarizer {

	@Autowired
	private DeviceContainer deviceContainer;

	public Set<PowerConsumptionRecord> summarizeRecords(
			List<CurrentRecord> currentRecords,
			DateIntervalGenerator dateIntervalGenerator) {
		// create the map that tracks the consumption record for each bucket
		Map<Date, Map<String, PowerConsumptionMeasurement>> powerRecordMap = new TreeMap<Date, Map<String, PowerConsumptionMeasurement>>();
		// get the iterator
		Iterator<CurrentRecord> iterator = currentRecords.iterator();
		// the last record
		CurrentRecord lastRecord = iterator.next();
		// iterate through all records
		while (true == iterator.hasNext()) {
			// get the next record
			CurrentRecord currentRecord = iterator.next();
			// calculate the time difference between the last record
			float timeDifferenceInSeconds = (float) (currentRecord
					.getTimestamp().getTime() - lastRecord.getTimestamp()
					.getTime()) / 1000.0f;
			// get the interval start time
			Date intervalStartDate = dateIntervalGenerator
					.flattenDate(currentRecord.getTimestamp());
			// get the power record
			Map<String, PowerConsumptionMeasurement> powerConsumptionMap = powerRecordMap
					.get(intervalStartDate);
			if (null == powerConsumptionMap) {
				// first record for the current interval, create + populate the
				// amp
				powerConsumptionMap = new HashMap<String, PowerConsumptionMeasurement>();
				for (CircuitEntity circuitEntity : deviceContainer
						.getDeviceEntity().getCircuits()) {
					PowerConsumptionMeasurement powerMeasurement = new PowerConsumptionMeasurement(
							circuitEntity.getName(), 0.0f);
					powerConsumptionMap.put(circuitEntity.getName(),
							powerMeasurement);
				}
				// store the map
				powerRecordMap.put(intervalStartDate, powerConsumptionMap);
			}
			// go through all current measurements and add the energy consumed
			// to the running count
			for (CurrentMeasurement currentMeasurement : currentRecord
					.getMeasurements()) {
				// find the circuit
				CircuitEntity circuitEntity = deviceContainer.getDeviceEntity()
						.getCircuitByName(currentMeasurement.getCircuit());
				// get the measurement
				PowerConsumptionMeasurement powerMeasurement = powerConsumptionMap
						.get(currentMeasurement.getCircuit());
				// calculate the power level
				float powerInWatts = circuitEntity.getVoltage()
						* currentMeasurement.getCurrent();
				// calculate the energy consumption
				float joules = powerInWatts * timeDifferenceInSeconds;
				// update the total
				powerMeasurement.setValue(powerMeasurement.getValue() + joules);

			}
			// store the current record
			lastRecord = currentRecord;
		}
		// create the return set
		Set<PowerConsumptionRecord> powerRecords = new TreeSet<PowerConsumptionRecord>();
		for (Map.Entry<Date, Map<String, PowerConsumptionMeasurement>> entry : powerRecordMap
				.entrySet()) {
			Set<PowerConsumptionMeasurement> measurements = new HashSet<PowerConsumptionMeasurement>();
			for (PowerConsumptionMeasurement measurement : entry.getValue()
					.values()) {
				measurements.add(measurement);
			}
			PowerConsumptionRecord powerRecord = new PowerConsumptionRecord(
					entry.getKey(), measurements);
			powerRecords.add(powerRecord);
		}
		// done
		return powerRecords;
	}

}
