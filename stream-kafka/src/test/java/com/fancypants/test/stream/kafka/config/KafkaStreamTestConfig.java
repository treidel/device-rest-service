package com.fancypants.test.stream.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.fancypants.common.CommonScanMe;
import com.fancypants.stream.kafka.StreamKafkaScanMe;
import com.fancypants.test.stream.TestStreamScanMe;

@Configuration
@ComponentScan(basePackageClasses = { CommonScanMe.class,
		TestStreamScanMe.class, StreamKafkaScanMe.class })
@PropertySource("classpath:/test.properties")
public class KafkaStreamTestConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
