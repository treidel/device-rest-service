package com.fancypants.common.config.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtils {

	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigUtils.class);

	public static String retrieveEnvVarOrFail(String var) {
		LOG.trace("retrieveEnvVarOrFail enter", "var", var);
		String value = System.getenv(var);
		if (null == value) {
			LOG.error("environment variable var=" + var + " not found");
			throw new IllegalArgumentException("var=" + var);
		}
		LOG.trace("retrieveEnvVarOrFail exit", "value", value);
		return value;
	}

	public static int retrieveEnvVarOrFailInt(String var) {
		LOG.trace("retrieveEnvVarOrFailInt enter", "var", var);
		String value = System.getenv(var);
		if (null == value) {
			LOG.error("environment variable var=" + var + " not found");
			throw new IllegalArgumentException("var=" + var);
		}
		try {
			int result = Integer.parseInt(value);
			LOG.trace("retrieveEnvVarOrFailInt exit {}", result);
			return result;
		} catch (NumberFormatException e) {
			throw new NumberFormatException("var=" + var + " not an integer");
		}
	}
}
