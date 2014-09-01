package com.fancypants.rest.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kinesis.model.Record;
import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;
import com.fancypants.data.device.kinesis.stream.StreamWriter;
import com.fancypants.rest.domain.CurrentRecord;
import com.fancypants.rest.mapping.CurrentRecordToRecordMapper;
import com.fancypants.rest.mapping.DeviceAndRecordToRecordEntityMapper;
import com.fancypants.rest.mapping.DeviceEntityAndRecordEntityToRecordMapper;
import com.fancypants.rest.request.DeviceContainer;

@Service
public class RecordService {

	@Autowired
	private DeviceEntityAndRecordEntityToRecordMapper recordEntityMapper;
	@Autowired
	private DeviceAndRecordToRecordEntityMapper recordMapper;
	@Autowired
	private CurrentRecordToRecordMapper queueMapper;
	@Autowired
	private RecordRepository recordRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceContainer deviceContainer;
	@Autowired
	@Qualifier("rawRecordStreamWriter")
	private StreamWriter rawRecordStreamWriter;

	public CurrentRecord findRecordForDevice(UUID uuid) {
		// create the record id for the query
		RecordId recordId = new RecordId();
		recordId.setDevice(deviceContainer.getDeviceEntity().getDevice());
		recordId.setUUID(uuid.toString());
		// execute the query
		RecordEntity recordEntity = recordRepository.get(recordId);
		if (null == recordEntity) {
			return null;
		}
		// map the data back to record
		CurrentRecord record = recordEntityMapper
				.convert(new ImmutablePair<DeviceEntity, RecordEntity>(
						deviceContainer.getDeviceEntity(), recordEntity));
		// done
		return record;
	}

	public List<CurrentRecord> findAllRecordsForDevice() {
		// query for all records for this device
		List<RecordEntity> recordEntities = recordRepository
				.findByDevice(deviceContainer.getDeviceEntity().getDevice());
		List<CurrentRecord> records = new ArrayList<CurrentRecord>(
				recordEntities.size());
		for (RecordEntity recordEntity : recordEntities) {
			CurrentRecord record = recordEntityMapper
					.convert(new ImmutablePair<DeviceEntity, RecordEntity>(
							deviceContainer.getDeviceEntity(), recordEntity));
			records.add(record);
		}
		return records;
	}

	public void bulkCreateRecords(Collection<CurrentRecord> currentRecords) {
		// find the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		for (CurrentRecord currentRecord : currentRecords) {
			RecordEntity recordEntity = recordMapper
					.convert(new ImmutablePair<DeviceEntity, CurrentRecord>(
							deviceEntity, currentRecord));
			// try to create the record
			boolean created = recordRepository.insert(recordEntity);
			if (true == created) {
				// not a dup, insert it into the queue for more processing
				Record record = queueMapper.convert(currentRecord);
				rawRecordStreamWriter.putRecord(record);				
			}
		}

	}

}
