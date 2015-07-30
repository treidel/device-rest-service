package com.fancypants.common.generators;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HourlyDateIntervalGenerator implements DateIntervalGenerator, Serializable {

	private static final long serialVersionUID = -8039878488860827287L;

	private static final Logger LOG = LoggerFactory.getLogger(HourlyDateIntervalGenerator.class);

	private static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

	@Override
	public Date flattenDate(Date input) {
		LOG.trace("flattenDate enter", "input", input);
		// create a calendar object using GMT timezone
		Calendar calendar = Calendar.getInstance(GMT_TIMEZONE);
		calendar.setTime(input);
		// find the start of the hour
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		// get the date representation
		Date startOfHour = calendar.getTime();
		LOG.trace("flattenDate exit", startOfHour);
		return startOfHour;
	}

}
