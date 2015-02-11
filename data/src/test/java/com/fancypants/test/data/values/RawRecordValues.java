package com.fancypants.test.data.values;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.RawMeasurementEntity;
import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.data.entity.RawRecordId;

@Component
public class RawRecordValues {
	public static RawRecordEntity RECORD1;
	public static RawRecordEntity RECORD2;
	public static final RawRecordId INVALID_RECORD_ID = new RawRecordId(
			"WXYZ7890", UUID.randomUUID());
	public static RawRecordEntity RECORDS[];

	static {
		// setup test data
		Map<Integer, RawMeasurementEntity> circuits = new TreeMap<Integer, RawMeasurementEntity>();
		for (int i = 1; i <= 16; i++) {
			RawMeasurementEntity measurement = new RawMeasurementEntity(i,
					120.0f, 30.0f);
			circuits.put(i, measurement);
		}
		// setup the test records
		RECORD1 = new RawRecordEntity(new RawRecordId("ABCD1234",
				UUID.randomUUID()), new Date(), 10.0f, circuits);
		RECORD2 = new RawRecordEntity(new RawRecordId("ABCD1234",
				UUID.randomUUID()), new Date(), 10.0f, circuits);

		// setup the list of records
		RECORDS = new RawRecordEntity[] { RECORD1, RECORD2 };

	}

}
