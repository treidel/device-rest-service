package com.fancypants.rest.mapping;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.rest.domain.Circuit;
import com.fancypants.rest.domain.Device;

@Component
public class DeviceEntityMapper implements EntityMapper<DeviceEntity, Device> {

	@Autowired
	private CircuitEntityMapper circuitMapper;

	@Override
	public DeviceEntity convert(Device device) {
		// create the set for the circuits
		Set<CircuitEntity> circuitEntities = new TreeSet<CircuitEntity>();
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
