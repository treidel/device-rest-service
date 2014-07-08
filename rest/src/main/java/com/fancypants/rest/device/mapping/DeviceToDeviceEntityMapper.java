package com.fancypants.rest.device.mapping;

import java.text.DateFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.rest.device.domain.Circuit;
import com.fancypants.rest.device.domain.Device;

@Component
public class DeviceToDeviceEntityMapper implements
		EntityMapper<DeviceEntity, Device> {

	@Autowired
	private CircuitToCircuitEntityMapper circuitMapper;
	
	@Autowired
	private DateFormat iso8601Format;

	@Override
	public DeviceEntity convert(Device device) {
		// create the entity
		DeviceEntity deviceEntity = new DeviceEntity();
		deviceEntity.setDevice(device.getName());
		deviceEntity.setLastModifiedTimestamp(iso8601Format.format(device.getLastModifiedTimestamp()));
		deviceEntity.setSerialNumber(device.getSerialNumber());
		// create the set for the circuits
		Set<CircuitEntity> circuitEntities = new HashSet<CircuitEntity>(device.getCircuits()
				.size());
		int count = 1;
		// this will iterate in the sorted order
		for (Circuit circuit : device.getCircuits()) {
			// create the circuit entity
			CircuitEntity circuitEntity = circuitMapper.convert(new ImmutablePair<Integer, Circuit>(count, circuit));
			// add it to the list
			circuitEntities.add(circuitEntity);
			// increment the assignment counter
			count++;
		}
		// store the circuits
		deviceEntity.setCircuits(circuitEntities);
		// done
		return deviceEntity;
	}

}
