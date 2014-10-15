package com.fancypants.data.device.dynamodb.test.cases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class AbstractTest {
	protected static final DateFormat iso8601DateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm'Z'");

	static {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		iso8601DateFormat.setTimeZone(tz);
	}

}
