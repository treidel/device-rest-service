package com.fancypants.rest.mapping;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.rest.domain.Circuit;

@Component
public class CircuitEntityMapper implements
		EntityMapper<CircuitEntity, Pair<Integer, Circuit>> {

	@Override
	public CircuitEntity convert(Pair<Integer, Circuit> entity) {
		// extract the info
		Circuit circuit = entity.getRight();
		int index = entity.getLeft();
		// create the circuit entity
		return new CircuitEntity(index, circuit.getName(),
				circuit.getVoltage(), circuit.getAmperage());
	}

}
