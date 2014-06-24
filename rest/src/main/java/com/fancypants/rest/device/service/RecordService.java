package com.fancypants.rest.device.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fancypants.rest.device.domain.Measurement;
import com.fancypants.rest.device.domain.Record;

@Service
public class RecordService {

	public Record findRecordForDevice(String deviceId, UUID uuid) {
		Set<Measurement> measurements = new HashSet<Measurement>(32);
		Measurement measurement = new Measurement("1-0", 0.5f);
		measurements.add(measurement);
		Record record = new Record(UUID.randomUUID(), new Date(), measurements);
		return record;
	}

	public Collection<Record> findAllRecordsForDevice(String deviceId) {
		Set<Measurement> measurements = new HashSet<Measurement>(32);
		Measurement measurement = new Measurement("1-0", 0.5f);
		measurements.add(measurement);
		Record record = new Record(UUID.randomUUID(), new Date(), measurements);
		Collection<Record> records = new LinkedList<Record>();
		records.add(record);
		return records;
	}

	public void createOrUpdateRecord(String deviceId, Record record) {

	}

}
