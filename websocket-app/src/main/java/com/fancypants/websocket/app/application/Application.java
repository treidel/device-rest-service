package com.fancypants.websocket.app.application;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fancypants.data.device.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.websocket.WebSocketScanMe;
import com.fancypants.websocket.app.WebSocketAppScanMe;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { WebSocketAppScanMe.class,
		WebSocketScanMe.class, DataDeviceScanMe.class,
		DataDeviceDynamoDBScanMe.class, DeviceScanMe.class})
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
