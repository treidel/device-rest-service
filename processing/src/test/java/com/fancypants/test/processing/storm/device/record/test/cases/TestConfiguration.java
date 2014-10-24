package com.fancypants.test.processing.storm.device.record.test.cases;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.fancypants.stream.kinesis.StreamKinesisScanMe;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { TestConfiguration.class,
		StreamKinesisScanMe.class })
public class TestConfiguration {

}
