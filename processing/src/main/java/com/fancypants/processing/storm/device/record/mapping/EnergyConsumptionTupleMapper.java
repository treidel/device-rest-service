package com.fancypants.processing.storm.device.record.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backtype.storm.tuple.Fields;

import com.fancypants.common.mapping.EntityMapper;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;

public class EnergyConsumptionTupleMapper implements
		EntityMapper<List<Object>, EnergyConsumptionRecordEntity>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1215563042377564751L;
	private static final int FIXED_FIELDS_COUNT = 2;

	@Override
	public List<Object> convert(EnergyConsumptionRecordEntity entity) {
		List<Object> values = new ArrayList<Object>(FIXED_FIELDS_COUNT
				+ (2 * DeviceEntity.MAX_CIRCUITS));
		// add the fixed fields
		values.add(entity.getDevice());
		values.add(entity.getDate().getTime());
		// add the circuits - this iterates in the order of the index
		for (Map.Entry<Integer, Float> entry : entity.getEnergy().entrySet()) {
			values.add(entry.getValue());
		}
		return values;
	}

	public static Fields getOutputFields() {
		List<String> fields = new ArrayList<String>(FIXED_FIELDS_COUNT
				+ DeviceEntity.MAX_CIRCUITS);
		fields.add(EnergyConsumptionRecordEntity.DEVICE_ATTRIBUTE);
		fields.add(EnergyConsumptionRecordEntity.DATE_ATTRIBUTE);
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			String field = EnergyConsumptionRecordEntity.ENERGY_IN_KWH_ATTRIBUTE_PREFIX
					+ i;
			fields.add(field);
		}
		return new Fields(fields);
	}
}
