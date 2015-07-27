package com.fancypants.common.generators;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DailyDateIntervalGenerator implements DateIntervalGenerator,
		Serializable {

	private static final long serialVersionUID = -8039878488860827287L;
	
	private static final Logger LOG = LoggerFactory.getLogger(DailyDateIntervalGenerator.class);

	@Override
	public Date flattenDate(Date input) {
		LOG.trace("flattenDate enter", "input", input);
		// create a calendar object with the timestamp's value
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(input);
		// find the start of the day
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		// get the date representation
		Date startOfDay = calendar.getTime();
		LOG.trace("flattenDate exit", startOfDay);
		return startOfDay;
	}

}
