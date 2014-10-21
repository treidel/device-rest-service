package com.fancypants.rest.app.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.fancypants.data.device.DataDeviceScanMe;
import com.fancypants.data.device.dynamodb.DataDeviceDynamoDBScanMe;
import com.fancypants.device.DeviceScanMe;
import com.fancypants.rest.RestScanMe;
import com.fancypants.rest.app.RestAppScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { RestScanMe.class, RestAppScanMe.class,
		DataDeviceScanMe.class, DataDeviceDynamoDBScanMe.class , DeviceScanMe.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
