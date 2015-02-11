package com.fancypants.test.data.values;

import java.util.Calendar;
import java.util.Date;
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
		// find the start time of the hour
		Date startOfHour = calendar.getTime();
		// find the nextday of the next
		calendar.add(Calendar.HOUR, 1);
		Date endOfHour = calendar.getTime();

		// setup the test records
		RECORD1 = new EnergyConsumptionRecordEntity(
				new EnergyConsumptionRecordId("ABCD1234", startOfHour));
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			RECORD1.setEnergy(i, 10.0f);
		}
		RECORD2 = new EnergyConsumptionRecordEntity(
				new EnergyConsumptionRecordId("ABCD1234", endOfHour));
		for (int i = 1; i <= DeviceEntity.MAX_CIRCUITS; i++) {
			RECORD2.setEnergy(i, 20.0f);
		}

		// setup the list of records
		RECORDS = new EnergyConsumptionRecordEntity[] { RECORD1, RECORD2 };
	}

}
