package com.fancypants.common.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@Configuration
public class CommonConfig {

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	private void init() {
		// configure the data serialization
		objectMapper.setDateFormat(new ISO8601DateFormat());
	}

	@Bean
	@ConditionalOnNotWebApplication
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper;
	}
}
