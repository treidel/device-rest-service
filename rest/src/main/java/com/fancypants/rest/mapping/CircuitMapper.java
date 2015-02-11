package com.fancypants.rest.mapping;

import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.rest.domain.Circuit;

@Component
public class CircuitMapper implements
		EntityMapper<Circuit, CircuitEntity> {

	@Override
	public Circuit convert(CircuitEntity entity) {
		// create the circuit object
		Circuit circuit = new Circuit(entity.getName(), entity.getVoltage(),
				entity.getAmperage());
		return circuit;
	}

}
