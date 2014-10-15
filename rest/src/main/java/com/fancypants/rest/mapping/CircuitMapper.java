package com.fancypants.rest.mapping;

import java.text.DateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.rest.domain.Circuit;

@Component
public class CircuitMapper implements
		EntityMapper<Circuit, CircuitEntity> {

	@Autowired
	private DateFormat iso8601Format;

	@Override
	public Circuit convert(CircuitEntity entity) {
		// create the circuit object
		Circuit circuit = new Circuit(entity.getName(), entity.getVoltage(),
				entity.getAmperage());
		return circuit;
	}

}
