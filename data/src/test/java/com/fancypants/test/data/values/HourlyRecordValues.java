package com.fancypants.test.data.values;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.fancypants.data.entity.DeviceEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordEntity;
import com.fancypants.data.entity.EnergyConsumptionRecordId;

@Component
public class HourlyRecordValues {
	public static EnergyConsumptionRecordEntity RECORD1;
	public static EnergyConsumptionRecordEntity RECORD2;
	public static EnergyConsumptionRecordEntity RECORDS[];

	static {

		// get the current time
		Date currentTime = new Date();
		// use GMT time
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		// populate the current time
		calendar.setTime(currentTime);
		// want times on hour boundary
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		// random number generator for usage
		Random rand = new Random();

		// list of records
		List<EnergyConsumptionRecordEntity> records = new ArrayList<>(24);
		for (int i = 0; i < 24; i++) {
			// get the timestamp
			Date timestamp = calendar.getTime();
			// create the record
			EnergyConsumptionRecordEntity record = new EnergyConsumptionRecordEntity(
					new EnergyConsumptionRecordId(
							DeviceValues.DEVICEENTITY.getDevice(), timestamp));
			for (int j = 1; j <= DeviceEntity.MAX_CIRCUITS; j++) {
				record.setEnergy(j, rand.nextFloat() * 100);
			}
			// insert record
			records.add(record);
			// find the nextday of the next
			calendar.add(Calendar.HOUR, 1);
		}
		// setup the list of records
		RECORDS = new EnergyConsumptionRecordEntity[24];
		records.toArray(RECORDS);
		RECORD1 = RECORDS[0];
		RECORD2 = RECORDS[1];
	}

}
