package com.fancypants.test.data.values;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.data.entity.DeviceEntity;

@Component
public class DeviceValues {
	public static DeviceEntity DEVICEENTITY;
	public static final String INVALID_DEVICE_NAME = "WXYZ7890";

	static {
		// setup the circuits
		Set<CircuitEntity> circuits = new HashSet<CircuitEntity>();
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			CircuitEntity circuit = new CircuitEntity(i, "1-" + i, 120.0f,
					10.0f);
			circuits.add(circuit);
		}
		// setup the device
		DEVICEENTITY = new DeviceEntity("ABCD1234", "000000001", circuits,
				new Date());
	}

}
