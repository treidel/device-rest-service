package com.fancypants.rest.mapping;

import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.rest.domain.Circuit;
import com.fancypants.rest.domain.Device;

@Component
public class DeviceMapper implements
		EntityMapper<Device, DeviceEntity> {

	@Override
	public Device convert(DeviceEntity entity) {
		// create the set for the circuits
		SortedSet<Circuit> circuits = new TreeSet<Circuit>();
		for (CircuitEntity circuitEntity : entity.getCircuits()) {
			// create the circuit object
			Circuit circuit = new Circuit(circuitEntity.getName(),
					circuitEntity.getVoltage(), circuitEntity.getAmperage());
			circuits.add(circuit);
		}

		// create + return the device
		Device device = new Device(entity.getDevice(),
				entity.getSerialNumber(), circuits);
		return device;

	}

}
