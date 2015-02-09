package com.fancypants.rest.app.application;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fancypants.app.AppScanMe;
import com.fancypants.data.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.rest.RestScanMe;
import com.fancypants.rest.app.RestAppScanMe;
import com.fancypants.usage.UsageScanMe;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { RestScanMe.class, RestAppScanMe.class,
		DataDeviceScanMe.class, DataDeviceDynamoDBScanMe.class,
		DeviceScanMe.class, AppScanMe.class, UsageScanMe.class })
public class Application {

	@Autowired
	MappingJackson2HttpMessageConverter httpMessageConverter;

	@PostConstruct
	public void init() {
		// tweak the serialization of dates
		httpMessageConverter.getObjectMapper().setDateFormat(
				new ISO8601DateFormat());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
