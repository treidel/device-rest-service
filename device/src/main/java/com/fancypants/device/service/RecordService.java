package com.fancypants.device.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.device.entity.CircuitEntity;
import com.fancypants.data.device.entity.DeviceEntity;
import com.fancypants.data.device.entity.RawRecordEntity;
import com.fancypants.data.device.entity.RawRecordId;
import com.fancypants.data.device.repository.DeviceRepository;
import com.fancypants.data.device.repository.RawRecordRepository;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.stream.writer.StreamWriter;

@Service
public class RecordService {

	private static final Logger LOG = LoggerFactory
			.getLogger(RecordService.class);

	@Autowired
	private RawRecordRepository recordRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private StreamWriter<RawRecordEntity> streamWriter;

	public RawRecordEntity findRecordForDevice(UUID uuid)
			throws AbstractServiceException {
		LOG.trace("findRecordForDevice entry", uuid);
		// create the record id for the query
		RawRecordId recordId = new RawRecordId(deviceContainer
				.getDeviceEntity().getDevice(), uuid);
		// execute the query
		RawRecordEntity recordEntity = recordRepository.findOne(recordId);
		if (null == recordEntity) {
			LOG.debug("findRecordForDevice record not found", recordId);
			throw new BusinessLogicException("record not found");
		}
		// done
		LOG.trace("findRecordForDevice exit", recordEntity);
		return recordEntity;
	}

	public Collection<RawRecordEntity> findRecordsForDevice() {
		LOG.trace("findRecordsForDevice entry");
		List<RawRecordEntity> entities = recordRepository
				.findAllForDevice(deviceContainer.getDeviceEntity().getDevice());
		LOG.trace("findRecordsForDevice exit", entities);
		return entities;
	}

	public void bulkCreateRecords(Collection<RawRecordEntity> entities)
			throws AbstractServiceException {
		LOG.trace("bulkCreateRecords entry", entities);
		// find the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		for (RawRecordEntity recordEntity : entities) {
			// data validation rule: record must contain data for all circuits
			for (CircuitEntity circuitEntity : deviceEntity.getCircuits()) {
				if (null == recordEntity.getCircuit(circuitEntity.getIndex())) {
					LOG.debug("bulkCreateRecords validation failure", entities);
					throw new DataValidationException("no data in record="
							+ recordEntity.getUUID().toString()
							+ " for circuit=" + circuitEntity.getIndex());
				}
			}
			// try to create the record
			boolean created = recordRepository.insert(recordEntity);
			if (true == created) {
				// not a dup, insert it into the queue for more processing
				streamWriter.putRecord(recordEntity.getDevice(), recordEntity);
			} else {
				LOG.debug("duplicate record", recordEntity);
			}
		}
		LOG.trace("bulkCreateRecords exit");
	}
}
