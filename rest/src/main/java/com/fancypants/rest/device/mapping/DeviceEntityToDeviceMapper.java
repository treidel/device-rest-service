package com.fancypants.rest.device.mapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.rest.device.domain.Circuit;
import com.fancypants.rest.device.domain.Device;

@Component
public class DeviceEntityToDeviceMapper implements
		EntityMapper<Device, DeviceEntity> {

	@Autowired
	private DateFormat iso8601Format;

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
		// parse the date
		Date lastModifiedTimestamp;
		try {
			lastModifiedTimestamp = iso8601Format.parse(entity
					.getLastModifiedTimestamp());
		} catch (ParseException e) {
			throw new IllegalStateException(e.getMessage());
		}
		// create + return the device
		Device device = new Device(entity.getDevice(),
				entity.getSerialNumber(), lastModifiedTimestamp, circuits);
		return device;
	}

}
