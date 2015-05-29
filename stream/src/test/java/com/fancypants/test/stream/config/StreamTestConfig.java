package com.fancypants.test.stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fancypants.data.entity.RawRecordEntity;
import com.fancypants.stream.writer.StreamWriter;
import com.fancypants.test.stream.writer.TestStreamWriter;

@Configuration
public class StreamTestConfig {
	@Bean
	public StreamWriter<RawRecordEntity> rawRecordStreamWriter() {
		TestStreamWriter<RawRecordEntity> streamWriter = new TestStreamWriter<RawRecordEntity>();
		return streamWriter;
	}
}
