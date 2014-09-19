package com.fancypants.rest.mapping;

import java.sql.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fancypants.data.device.dynamodb.entity.RawRecordEntity;
import com.fancypants.stream.device.kinesis.entity.RawRecord;

@Component
public class RawRecordMapper implements
		EntityMapper<RawRecord, RawRecordEntity> {

	@Override
	public RawRecord convert(RawRecordEntity entity) {
		// create the raw record
		RawRecord record = new RawRecord(entity.getDevice(),
				UUID.fromString(entity.getUUID()), Date.valueOf(entity
						.getTimestamp()), entity.getCircuits(),
				entity.getDuration());
		// done
		return record;
	}

}
