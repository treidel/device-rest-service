package com.fancypants.processing.storm.device.record.mapping;

import java.io.Serializable;
import java.text.ParsePosition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.tuple.TridentTuple;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.entity.PowerConsumptionRecordId;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

public class PowerConsumptionRecordEntityMapper implements
		EntityMapper<PowerConsumptionRecordEntity, TridentTuple>, Serializable {

	private static final long serialVersionUID = -2803119007375752938L;
	private static final Logger LOG = LoggerFactory
			.getLogger(PowerConsumptionRecordEntityMapper.class);

	@Override
	public PowerConsumptionRecordEntity convert(TridentTuple tuple) {
		LOG.trace("convert entry", tuple);
		try {
			// create the id
			PowerConsumptionRecordId id = new PowerConsumptionRecordId(
					tuple.getStringByField(PowerConsumptionRecordEntity.DEVICE_ATTRIBUTE),
					ISO8601Utils.parse(
							tuple.getStringByField(PowerConsumptionRecordEntity.DATE_ATTRIBUTE),
							new ParsePosition(0)));

			// create the entity
			PowerConsumptionRecordEntity entity = new PowerConsumptionRecordEntity(
					id);
			LOG.trace("convert exit", entity);
			return entity;
		} catch (java.text.ParseException e) {
			LOG.error("parse error", e);
			return null;
		}
	}
}
