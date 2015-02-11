package com.fancypants.rest.app.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fancypants.app.AppScanMe;
import com.fancypants.common.CommonScanMe;
import com.fancypants.data.DataScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.rest.RestScanMe;
import com.fancypants.rest.app.RestAppScanMe;
import com.fancypants.usage.UsageScanMe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { CommonScanMe.class, RestScanMe.class,
		RestAppScanMe.class, DataScanMe.class, DeviceScanMe.class,
		AppScanMe.class, UsageScanMe.class })
public class WebConfig {

	@Autowired
	private MappingJackson2HttpMessageConverter httpMessageConverter;

	@Autowired
	private ObjectMapper mapper;

	@PostConstruct
	public void init() {
		// tweak the serialization of dates
		httpMessageConverter.getObjectMapper().setDateFormat(new ISO8601DateFormat());
	}
}
