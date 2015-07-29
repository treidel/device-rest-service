package com.fancypants.records.service;

import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.common.exception.AbstractServiceException;
import com.fancypants.common.exception.BusinessLogicException;
import com.fancypants.common.exception.DataPersistenceException;
import com.fancypants.common.exception.DataValidationException;
import com.fancypants.data.entity.CircuitEntity;
import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;
import com.fancypants.data.repository.RawRecordRepository;
import com.fancypants.stream.exception.StreamException;
import com.fancypants.stream.writer.StreamWriter;

@Service
public class RecordService {

	private static final Logger LOG = LoggerFactory.getLogger(RecordService.class);

	@Autowired
	private RawRecordRepository recordRepository;
	@Autowired
	private StreamWriter<RawRecordEntity> streamWriter;

	public RawRecordEntity findRecordForDevice(DeviceEntity deviceEntity, UUID uuid) throws AbstractServiceException {
		LOG.trace("findRecordForDevice entry", uuid);
		// create the record id for the query
		RawRecordId recordId = new RawRecordId(deviceEntity.getDevice(), uuid);
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

	public void bulkCreateRecords(DeviceEntity deviceEntity, Collection<RawRecordEntity> entities)
			throws AbstractServiceException {
		LOG.trace("bulkCreateRecords entry", entities);
		for (RawRecordEntity recordEntity : entities) {
			// data validation rule: record must contain data for all circuits
			for (CircuitEntity circuitEntity : deviceEntity.getCircuits()) {
				if (null == recordEntity.getCircuit(circuitEntity.getIndex())) {
					LOG.debug("bulkCreateRecords validation failure", entities);
					throw new DataValidationException("no data in record=" + recordEntity.getId().toString()
							+ " for circuit=" + circuitEntity.getIndex());
				}
			}
			// insert it into the queue for more processing, dups will be
			// detected + eliminated later
			try {
				streamWriter.putRecord(recordEntity.getId().getDevice(), recordEntity);
			} catch (StreamException e) {
				LOG.error("stream error", e);
				throw new DataPersistenceException(
						"unable to write data to persistent storage for record=" + recordEntity.getId().toString());
			}
		}
		LOG.trace("bulkCreateRecords exit");
	}
}
