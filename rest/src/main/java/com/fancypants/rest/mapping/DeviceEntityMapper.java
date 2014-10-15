package com.fancypants.rest.mapping;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.rest.domain.Circuit;
import com.fancypants.rest.domain.Device;

@Component
public class DeviceEntityMapper implements EntityMapper<DeviceEntity, Device> {

	@Autowired
	private CircuitEntityMapper circuitMapper;

	@Autowired
	private DateFormat iso8601Format;

	@Override
	public DeviceEntity convert(Device device) {
		// create the set for the circuits
		Set<CircuitEntity> circuitEntities = new HashSet<CircuitEntity>(device
				.getCircuits().size());
		int count = 1;
		// this will iterate in the sorted order
		for (Circuit circuit : device.getCircuits()) {
			// create the circuit entity
			CircuitEntity circuitEntity = circuitMapper
					.convert(new ImmutablePair<Integer, Circuit>(count, circuit));
			// add it to the list
			circuitEntities.add(circuitEntity);
			// increment the assignment counter
			count++;
		}

		// create the entity
		DeviceEntity deviceEntity = new DeviceEntity(device.getName(),
				device.getSerialNumber(), circuitEntities, new Date());
		// done
		return deviceEntity;
	}

}
