package com.fancypants.processing.storm.device.record.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.tuple.TridentTuple;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordId;

public class EnergyConsumptionEntityMapper implements
		EntityMapper<EnergyConsumptionRecordEntity, TridentTuple>, Serializable {

	private static final long serialVersionUID = -2803119007375752938L;
	private static final Logger LOG = LoggerFactory
			.getLogger(EnergyConsumptionEntityMapper.class);

	public static final String ATTRIBUTES[] = new String[2 + DeviceEntity.MAX_CIRCUITS];

	static {
		// configure the energy attributes
		List<String> attributes = new ArrayList<String>(ATTRIBUTES.length);
		// add fixed fields
		attributes.add(EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE);
		attributes.add(EnergyConsumptionRecordEntity.DATE_ATTRIBUTE);
		// add the circuits
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			attributes
					.add(EnergyConsumptionRecordEntity.ENERGY_IN_KWH_ATTRIBUTE_PREFIX
							+ i);
		}
		// populate the arrays
		attributes.toArray(ATTRIBUTES);
	}

	@Override
	public EnergyConsumptionRecordEntity convert(TridentTuple tuple) {
		LOG.trace("convert entry", tuple);
		// create the id
		EnergyConsumptionRecordId id = new EnergyConsumptionRecordId(
				tuple.getStringByField(EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE),
				new Date(
						tuple.getLongByField(EnergyConsumptionRecordEntity.DATE_ATTRIBUTE)));

		// create the entity
		EnergyConsumptionRecordEntity entity = new EnergyConsumptionRecordEntity(
				id);
		// populate the data
		for (int i = 1; i < DeviceEntity.MAX_CIRCUITS; i++) {
			String field = EnergyConsumptionRecordEntity.ENERGY_IN_KWH_ATTRIBUTE_PREFIX
					+ i;
			float value = tuple.getFloatByField(field);
			entity.setEnergy(i, value);
		}
		LOG.trace("convert exit", entity);
		return entity;
	}
}
