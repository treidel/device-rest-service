package com.fancypants.test.processing.storm.device.record.test.cases;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import storm.trident.spout.ITridentSpout;
import storm.trident.testing.FeederBatchSpout;

import com.fancypants.storm.device.record.mapping.RawRecordTupleMapper;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class })
public class TestConfiguration {

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
