package com.fancypants.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@Configuration
public class CommonConfig {

	@Bean
	@Lazy
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// configure the data serialization
		objectMapper.setDateFormat(new ISO8601DateFormat());
		return objectMapper;

	}
}
