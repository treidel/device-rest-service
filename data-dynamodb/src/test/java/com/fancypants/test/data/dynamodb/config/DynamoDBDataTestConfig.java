package com.fancypants.test.data.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fancypants.common.CommonScanMe;
import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.data.dynamodb.TestDynamoDBDataScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class, TestDataScanMe.class,
		TestDynamoDBDataScanMe.class })
public class DynamoDBDataTestConfig {

	@Bean
	public String tablePrefix() {
		return "test";
	}
}
