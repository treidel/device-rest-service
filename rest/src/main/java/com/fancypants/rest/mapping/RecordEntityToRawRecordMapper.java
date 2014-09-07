package com.fancypants.rest.mapping;

import java.sql.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.RecordEntity;
import com.fancypants.data.device.kinesis.entity.RawRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RecordEntityToRawRecordMapper implements
		EntityMapper<RawRecord, RecordEntity> {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public RawRecord convert(RecordEntity entity) {
		// create the raw record
		RawRecord record = new RawRecord(entity.getDevice(),
				UUID.fromString(entity.getUUID()), Date.valueOf(entity
						.getTimestamp()), entity.getCircuits(),
				entity.getDuration());
		// done
		return record;
	}

}
