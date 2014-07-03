package com.fancypants.rest.device.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.dynamodb.entity.RecordId;
import com.fancypants.data.device.dynamodb.repository.RecordRepository;
import com.fancypants.rest.device.domain.Device;
import com.fancypants.rest.device.domain.Record;
import com.fancypants.rest.device.mapping.DeviceAndRecordEntityToRecordMapper;
import com.fancypants.rest.device.mapping.DeviceAndRecordToRecordEntityMapper;

@Service
public class RecordService {

	private @Autowired
	DeviceAndRecordEntityToRecordMapper recordEntityMapper;
	private @Autowired
	DeviceAndRecordToRecordEntityMapper recordMapper;
	private @Autowired
	RecordRepository recordRepository;

	public Record findRecordForDevice(Device device, UUID uuid) {
		// create the record id for the query
		RecordId recordId = new RecordId();
		recordId.setDevice(device.getName());
		recordId.setUUID(uuid.toString());
		// execute the query
		RecordEntity recordEntity = recordRepository.findOne(recordId);
		// map the data back to record
		Record record = recordEntityMapper
				.convert(new ImmutablePair<Device, RecordEntity>(device,
						recordEntity));
		// done
		return record;
	}

	public Collection<Record> findAllRecordsForDevice(Device device) {
		Collection<RecordEntity> recordEntities = recordRepository
				.findByDevice(device.getName());
		Collection<Record> records = new ArrayList<Record>(
				recordEntities.size());
		for (RecordEntity recordEntity : recordEntities) {
			Record record = recordEntityMapper
					.convert(new ImmutablePair<Device, RecordEntity>(device,
							recordEntity));
			records.add(record);
		}
		return records;
	}

	public void bulkCreateRecords(Device device, Collection<Record> records) {
		Collection<RecordEntity> recordEntities = new ArrayList<RecordEntity>(
				records.size());
		for (Record record : records) {
			RecordEntity recordEntity = recordMapper
					.convert(new ImmutablePair<Device, Record>(device, record));
			recordEntities.add(recordEntity);
		}
		recordRepository.save(recordEntities);
	}

}
