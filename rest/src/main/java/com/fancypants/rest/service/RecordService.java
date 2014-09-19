package com.fancypants.rest.service;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.CircuitEntity;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.data.device.dynamodb.entity.RawRecordId;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.data.device.dynamodb.repository.RawRecordRepository;
import com.fancypants.rest.domain.CurrentRecord;
import com.fancypants.rest.exception.AbstractServiceException;
import com.fancypants.rest.exception.BusinessLogicException;
import com.fancypants.rest.mapping.CurrentRecordMapper;
import com.fancypants.rest.mapping.RawRecordMapper;
import com.fancypants.rest.mapping.RecordEntityMapper;
import com.fancypants.rest.request.DeviceContainer;
import com.fancypants.stream.device.kinesis.entity.RawRecord;
import com.fancypants.stream.device.kinesis.stream.StreamWriter;

@Service
public class RecordService {

	@Autowired
	private CurrentRecordMapper recordEntityMapper;
	@Autowired
	private RecordEntityMapper recordMapper;
	@Autowired
	private RawRecordMapper rawMapper;
	@Autowired
	private RawRecordRepository recordRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	private StreamWriter<RawRecord> streamWriter;

	public CurrentRecord findRecordForDevice(UUID uuid)
			throws AbstractServiceException {
		// create the record id for the query
		RawRecordId recordId = new RawRecordId();
		recordId.setDevice(deviceContainer.getDeviceEntity().getDevice());
		recordId.setUUID(uuid.toString());
		// execute the query
		RawRecordEntity recordEntity = recordRepository.get(recordId);
		if (null == recordEntity) {
			return null;
		}
		// map the data back to record
		CurrentRecord record = recordEntityMapper
				.convert(new ImmutablePair<DeviceEntity, RawRecordEntity>(
						deviceContainer.getDeviceEntity(), recordEntity));
		// done
		return record;
	}

	public void bulkCreateRecords(Collection<CurrentRecord> records)
			throws AbstractServiceException {
		// find the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		for (CurrentRecord record : records) {
			// convert into an internal representation
			RawRecordEntity recordEntity = recordMapper
					.convert(new ImmutablePair<DeviceEntity, CurrentRecord>(
							deviceEntity, record));
			// business rule: record must contain data for all circuits
			for (CircuitEntity circuitEntity : deviceEntity.getCircuits()) {
				if (null == recordEntity.getCircuit(circuitEntity.getIndex())) {
					throw new BusinessLogicException("no data in record="
							+ record.getUUID().toString() + " for circuit="
							+ circuitEntity.getIndex());
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
