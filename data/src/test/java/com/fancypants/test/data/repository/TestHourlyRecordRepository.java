package com.fancypants.test.data.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordId;
import com.fancypants.data.repository.HourlyRecordRepository;

@Component
public class TestHourlyRecordRepository
		extends
		AbstractTestRepository<EnergyConsumptionRecordEntity, EnergyConsumptionRecordId>
		implements HourlyRecordRepository {

	private static final long serialVersionUID = -1539444950934709972L;

	private static final Map<String, SortedMap<Date, EnergyConsumptionRecordEntity>> DEVICES = new HashMap<String, SortedMap<Date, EnergyConsumptionRecordEntity>>();

	public TestHourlyRecordRepository() {
		super(EnergyConsumptionRecordEntity.class);
	}

	@Override
	public <S extends EnergyConsumptionRecordEntity> S save(S entity) {
		SortedMap<Date, EnergyConsumptionRecordEntity> records = DEVICES
				.get(entity.getDevice());
		if (null == records) {
			records = new TreeMap<Date, EnergyConsumptionRecordEntity>();
			DEVICES.put(entity.getDevice(), records);
		}
		records.put(entity.getDate(), entity);
		return super.save(entity);
	}

	@Override
	public List<EnergyConsumptionRecordEntity> findByDevice(String device) {
		SortedMap<Date, EnergyConsumptionRecordEntity> recordsMap = DEVICES
				.get(device);
		if (null != recordsMap) {
			return new ArrayList<EnergyConsumptionRecordEntity>(
					recordsMap.values());
		}
		return Arrays.asList(new EnergyConsumptionRecordEntity[0]);
	}

	@Override
	public void deleteAllForDevice(String device) {
		Map<Date, EnergyConsumptionRecordEntity> records = DEVICES
				.remove(device);
		if (null != records) {
			for (EnergyConsumptionRecordEntity record : records.values()) {
				delete(record.getId());
			}
		}
	}

	@Override
	public void insertOrIncrement(EnergyConsumptionRecordEntity record) {
		// get the current record
		EnergyConsumptionRecordEntity current = findOne(record.getId());
		if (null != current) {
			// add into it
			sum(current.getEnergy(), record.getEnergy());
		} else {
			// save, its new
			save(record);
		}
	}

	private void sum(Map<Integer, Float> orig, Map<Integer, Float> update) {
		for (Map.Entry<Integer, Float> entry : orig.entrySet()) {
			float value = entry.getValue() + orig.get(entry.getKey());
			entry.setValue(value);
		}
	}
}
