package com.fancypants.rest.device.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.DeviceEntity;
import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.DeviceRepository;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;
import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.mapping.DeviceAndRecordToRecordEntityMapper;
import com.fancypants.rest.device.mapping.DeviceEntityAndRecordEntityToRecordMapper;
import com.fancypants.rest.device.request.DeviceContainer;

@Service
public class RecordService {

	@Autowired
	private DeviceEntityAndRecordEntityToRecordMapper recordEntityMapper;
	@Autowired
	private DeviceAndRecordToRecordEntityMapper recordMapper;
	@Autowired
	private RecordRepository recordRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private DeviceContainer deviceContainer;

	public Record findRecordForDevice(UUID uuid) {
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
		Record record = recordEntityMapper
				.convert(new ImmutablePair<DeviceEntity, RecordEntity>(
						deviceContainer.getDeviceEntity(), recordEntity));
		// done
		return record;
	}

	public Collection<Record> findAllRecordsForDevice() {
		// query for all records for this device
		Collection<RecordEntity> recordEntities = recordRepository
				.findByDevice(deviceContainer.getDeviceEntity().getDevice());
		Collection<Record> records = new ArrayList<Record>(
				recordEntities.size());
		for (RecordEntity recordEntity : recordEntities) {
			Record record = recordEntityMapper
					.convert(new ImmutablePair<DeviceEntity, RecordEntity>(
							deviceContainer.getDeviceEntity(), recordEntity));
			records.add(record);
		}
		return records;
	}

	public void bulkCreateRecords(Collection<Record> records) {
		// find the device
		DeviceEntity deviceEntity = deviceContainer.getDeviceEntity();
		// create the list for the record entities
		Collection<RecordEntity> recordEntities = new ArrayList<RecordEntity>(
				records.size());
		for (Record record : records) {
			RecordEntity recordEntity = recordMapper
					.convert(new ImmutablePair<DeviceEntity, Record>(
							deviceEntity, record));
			recordEntities.add(recordEntity);
		}
		// bulk write the records
		recordRepository.save(recordEntities);
	}

}
