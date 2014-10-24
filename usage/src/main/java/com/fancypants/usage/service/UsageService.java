package com.fancypants.usage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.device.entity.EnergyConsumptionRecordId;
import com.fancypants.data.device.repository.HourlyRecordRepository;

@Service
public class UsageService {

	@Autowired
	private HourlyRecordRepository hourlyRepository;

	public List<EnergyConsumptionRecordEntity> getHourlyRecords(
			DeviceEntity deviceEntity) {
		// query for all hourly records
		List<EnergyConsumptionRecordEntity> entities = hourlyRepository
				.findByDevice(deviceEntity.getDevice());
		// return the set
		return entities;
	}

	public EnergyConsumptionRecordEntity getHourlyRecord(
			DeviceEntity deviceEntity, Date date)
			throws AbstractServiceException {
		EnergyConsumptionRecordId id = new EnergyConsumptionRecordId(
				deviceEntity.getDevice(), date);
		EnergyConsumptionRecordEntity entity = hourlyRepository.findOne(id);
		if (null == entity) {
			throw new BusinessLogicException("record not found for date="
					+ date);
		}
		return entity;
	}
}
