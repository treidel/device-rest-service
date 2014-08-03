package com.fancypants.rest.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;
import com.fancypants.rest.domain.CurrentRecord;
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
	@Qualifier("recordRepository")
	private RecordRepository recordRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceContainer deviceContainer;

	public CurrentRecord findRecordForDevice(UUID uuid) {
		// create the record id for the query
		RecordId recordId = new RecordId();
		recordId.setDevice(deviceContainer.getDeviceEntity().getDevice());
		recordId.setUUID(uuid.toString());
		// execute the query
		RecordEntity recordEntity = recordRepository.findOne(recordId);
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

	public void bulkCreateRecords(Collection<CurrentRecord> records) {
		// find the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// create the list for the record entities
		Collection<RecordEntity> recordEntities = new ArrayList<RecordEntity>(
				records.size());
		for (CurrentRecord record : records) {
			RecordEntity recordEntity = recordMapper
					.convert(new ImmutablePair<DeviceEntity, CurrentRecord>(
							deviceEntity, record));
			recordEntities.add(recordEntity);
		}
		// bulk write the records
		recordRepository.save(recordEntities);
	}

}
