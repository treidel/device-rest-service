package com.fancypants.rest.usage;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class MonthlyDateIntervalGenerator implements DateIntervalGenerator {

	@Override
	public Date flattenDate(Date input) {
		// create a calendar object with the timestamp's value
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(input);
		// find the first date of the month
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		// get the date representation
		Date startOfMonth = calendar.getTime();
		return startOfMonth;
	}

}
