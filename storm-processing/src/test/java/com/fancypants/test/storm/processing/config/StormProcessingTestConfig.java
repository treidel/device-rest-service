package com.fancypants.test.storm.processing.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import storm.trident.spout.ITridentSpout;
import storm.trident.testing.FeederBatchSpout;

import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;
import com.fancypants.test.data.TestDataScanMe;
import com.fancypants.test.message.TestMessageScanMe;
import com.fancypants.test.storm.processing.TestStormProcessingScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { StormProcessingTestConfig.class,
		TestDataScanMe.class, TestMessageScanMe.class,
		TestStormProcessingScanMe.class })
public class StormProcessingTestConfig {

	@SuppressWarnings("rawtypes")
	@Bean
	public ITridentSpout spout() {
		return new FeederBatchSpout(RawRecordTupleMapper.getOutputFields()
				.toList());
	}

	@Bean
	public String tablePrefix() {
		return "test";
	}

}
