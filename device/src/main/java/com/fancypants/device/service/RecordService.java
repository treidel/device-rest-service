package com.fancypants.device.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordId;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.data.device.dynamodb.repository.RawRecordRepository;
import com.fancypants.device.container.DeviceContainer;
import com.fancypants.device.mapping.RawRecordMapper;
import com.fancypants.stream.kinesis.entity.RawRecord;
import com.fancypants.stream.kinesis.writer.StreamWriter;

@Service
public class RecordService {

	@Autowired
	private RawRecordRepository recordRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private RawRecordMapper rawMapper;
	@Autowired
	private StreamWriter<RawRecord> streamWriter;

	public RawRecordEntity findRecordForDevice(UUID uuid)
			throws AbstractServiceException {
		// create the record id for the query
		RawRecordId recordId = new RawRecordId();
		recordId.setDevice(deviceContainer.getDeviceEntity().getDevice());
		recordId.setUUID(uuid.toString());
		// execute the query
		RawRecordEntity recordEntity = recordRepository.findOne(recordId);
		if (null == recordEntity) {
			throw new BusinessLogicException("record not found");
		}
		// done
		return recordEntity;
	}

	public Collection<RawRecordEntity> findRecordsForDevice() {
		List<RawRecordEntity> entities = recordRepository
				.findAllForDevice(deviceContainer.getDeviceEntity().getDevice());
		return entities;
	}

	public void bulkCreateRecords(Collection<RawRecordEntity> entities)
			throws AbstractServiceException {
		// find the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		for (RawRecordEntity recordEntity : entities) {
			// data validation rule: record must contain data for all circuits
			for (CircuitEntity circuitEntity : deviceEntity.getCircuits()) {
				if (null == recordEntity.getCircuit(circuitEntity.getIndex())) {
					throw new DataValidationException("no data in record="
							+ recordEntity.getUUID().toString()
							+ " for circuit=" + circuitEntity.getIndex());
				}
			}
			// try to create the record
			boolean created = recordRepository.insert(recordEntity);
			if (true == created) {
				// map into a raw record
				RawRecord rawRecord = rawMapper.convert(recordEntity);
				// not a dup, insert it into the queue for more processing
				streamWriter.putRecord(rawRecord);
			}
		}
	}
}
