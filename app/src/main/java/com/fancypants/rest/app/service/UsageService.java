package com.fancypants.rest.app.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.PowerConsumptionRecordEntity;
import com.fancypants.data.device.dynamodb.repository.MonthlyRecordRepository;
import com.fancypants.rest.domain.PowerConsumptionRecord;
import com.fancypants.rest.mapping.PowerConsumptionRecordMapper;
import com.fancypants.rest.request.DeviceContainer;

@Service
public class UsageService {

	@Autowired
	private DeviceContainer deviceContainer;

	@Autowired
	private MonthlyRecordRepository monthlyRepository;

	private final PowerConsumptionRecordMapper mapper = new PowerConsumptionRecordMapper();

	public Set<PowerConsumptionRecord> getMonthlyRecords() {
		// query for all monthly records
		List<PowerConsumptionRecordEntity> entities = monthlyRepository
				.findByDevice(deviceContainer.getDevice().getName());
		// create the result set
		Set<PowerConsumptionRecord> records = new HashSet<PowerConsumptionRecord>(
				entities.size());
		for (PowerConsumptionRecordEntity entity : entities) {
			PowerConsumptionRecord record = mapper
					.convert(new ImmutablePair<DeviceEntity, PowerConsumptionRecordEntity>(
							deviceContainer.getDeviceEntity(), entity));
			records.add(record);
		}
		// return the set
		return records;
	}
}
